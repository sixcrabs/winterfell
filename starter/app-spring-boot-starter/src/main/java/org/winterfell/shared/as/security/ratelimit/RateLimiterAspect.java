package org.winterfell.shared.as.security.ratelimit;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.winterfell.shared.as.advice.ex.WebApiException;
import org.winterfell.shared.as.advice.response.ErrorResponse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 限流 aop
 * </p>
 *
 * @author Alex
 * @since 2025/11/27
 */
@Slf4j
@Aspect
public class RateLimiterAspect {

    private final Map<String, LocalRateLimiter> localRateLimiters = new ConcurrentHashMap<>();


    @Pointcut(value = "@annotation(org.winterfell.shared.as.security.ratelimit.RateLimiter)")
    public void rateLimit() {
    }

    @Around("rateLimit()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object result = null;
        try {
            Class<?> targetClass = joinPoint.getSignature().getDeclaringType();
            String methodName = joinPoint.getSignature().getName();
            Method method = getCurrentMethod(joinPoint);
            RateLimiter rateLimiterAnno = method.getAnnotation(RateLimiter.class);
            switch (rateLimiterAnno.type()) {
                case local: {
                    // 以 class + method + parameters 为 key，避免重载、重写带来的混乱
                    String key = targetClass.getName() + "." + methodName + Arrays.toString(method.getParameterTypes());
                    LocalRateLimiter rateLimiter = localRateLimiters.get(key);
                    if (null == rateLimiter) {
                        // 获取限定的流量 防止过高的并发
                        localRateLimiters.putIfAbsent(key, new LocalRateLimiter(rateLimiterAnno.value()));
                        rateLimiter = localRateLimiters.get(key);
                    }
                    boolean b = rateLimiter.tryAcquire();
                    if (b) {
                        log.debug("[local_rate_limiter] get token");
                        result = joinPoint.proceed();
                    } else {
                        log.warn("[local_rate_limiter] rate limited now");
                        throw new WebApiException(ErrorResponse.REQUEST_RATE_LIMITED.getCode(),
                                ErrorResponse.REQUEST_RATE_LIMITED.getMsg());
                    }
                    break;
                }
                case sentinel: {
                    int limitCount = rateLimiterAnno.value();
                    String resourceName = method.getName();
                    initFlowRule(resourceName, limitCount);
                    Entry entry = null;
                    try {
                        entry = SphU.entry(resourceName);
                        try {
                            result = joinPoint.proceed();
                        } catch (Throwable throwable) {
                            log.error("sentinel_rate_limiter_error: {}", throwable.getMessage());
                        }
                    } catch (BlockException ex) {
                        // 资源访问阻止，被限流或被降级 在此处进行相应的处理操作
                        log.warn("[sentinel_rate_limiter] request blocked now");
                        throw new WebApiException(ErrorResponse.REQUEST_RATE_LIMITED.getCode(),
                                ErrorResponse.REQUEST_RATE_LIMITED.getMsg());
                    } catch (Exception e) {
                        Tracer.traceEntry(e, entry);
                    } finally {
                        if (entry != null) {
                            entry.exit();
                        }
                    }
                    break;
                }
                default: {
                    return joinPoint.proceed();
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }


    private Method getCurrentMethod(JoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getSignature().getDeclaringType();
        Object[] arguments = joinPoint.getArgs();
        return RateLimitUtil.getMethod(targetClass, joinPoint.getSignature().getName(), arguments);
    }


    private static void initFlowRule(String resourceName, int limitCount) {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        //设置受保护的资源
        rule.setResource(resourceName);
        //设置流控规则 QPS
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //设置受保护的资源阈值
        rule.setCount(limitCount);
        rules.add(rule);
        //加载配置好的规则
        FlowRuleManager.loadRules(rules);
    }
}