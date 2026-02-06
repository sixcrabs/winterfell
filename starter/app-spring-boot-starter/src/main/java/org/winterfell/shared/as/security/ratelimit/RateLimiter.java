package org.winterfell.shared.as.security.ratelimit;

import java.lang.annotation.*;

/**
 * <p>
 * 自行实现的限流注解{@linkplain LocalRateLimiter}
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/8/2
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    /**
     * 限流实现类型
     * @return
     */
    RateLimiterType type() default RateLimiterType.local;

    /**
     * qps 受保护的资源阈值
     * @return
     */
    int value() default 10;
}