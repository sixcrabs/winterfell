package org.winterfell.misc.remote.mrc.support;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * 定制client的熔断器参数
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/10/12
 */
@Target({})
@Retention(RUNTIME)
public @interface MrClientCircuitBreakerParam {

    /**
     * 是否启用
     *
     * @return
     */
    boolean enabled() default false;

    /**
     * 失败比例
     *
     * @return
     */
    int failureRateThreshold() default 50;

    /**
     * 等待时间
     *
     * @return
     */
    int waitDurationInOpenStateSeconds() default 30;

    /**
     * 半开状态允许通过请求数
     *
     * @return
     */
    int permittedNumberOfCallsInHalfOpenState() default 4;


}
