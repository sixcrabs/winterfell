package org.winterfell.shared.as.security.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * <p>
 * 方法记录拦截切面
 * </p>
 *
 * @author Alex
 * @since 2025/10/14
 */
@Component
@Aspect
public class MethodLoggerAspect {

    public static final Logger logger = LoggerFactory.getLogger(MethodLoggerAspect.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 只拦截标有 @MethodLogger 的方法
    @Pointcut("@annotation(org.winterfell.shared.as.security.aop.MethodLogger)")
    public void annotatedMethod() {
    }

    /**
     * 打印方法参数 耗时等信息
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("annotatedMethod()")
    public Object monitor(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        String className = signature.getDeclaringTypeName();
        String methodName = method.getName();
        String args = MAPPER.writeValueAsString(pjp.getArgs());
        String module = method.getAnnotation(MethodLogger.class).module();

        Object result = null;
        Throwable ex = null;
        try {
            result = pjp.proceed();
            return result;
        } catch (Throwable t) {
            ex = t;
            throw t;
        } finally {
            long cost = System.currentTimeMillis() - start;
            String resp = (ex == null) ? MAPPER.writeValueAsString(result)
                    : "{\"error\":\"" + ex.getClass().getSimpleName() + "\"}";

            logger.info("\n========== [{}] ========== \n>>> location: {}.{} \n>>> params: {} \n>>> response: {}\n>>> cost: {} ms \n==========================",
                    module, className, methodName, args, resp, cost);
        }
    }
}
