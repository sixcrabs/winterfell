package org.winterfell.misc.remote.mrc;

import org.winterfell.misc.remote.mrc.interceptor.MrcInterceptor;
import org.winterfell.misc.remote.mrc.interceptor.NopeMrcInterceptor;
import org.winterfell.misc.remote.mrc.loadbalance.impl.RoundRobinLoadBalancer;
import org.winterfell.misc.remote.mrc.support.MrClientCircuitBreakerParam;
import org.winterfell.misc.remote.mrc.support.MrClientRetryParam;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * <p>
 * Annotation for interfaces declaring that a REST client with that interface should be
 *  created (e.g. for autowiring into another component)
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/4/9
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MrClient {

    /**
     * The service id, must be unique
     * @return
     */
    @AliasFor("name")
    String value() default "";

    /**
     * The service id, Synonym for {@link #value() value}.
     * @return
     */
    @AliasFor("value")
    String name() default "";

    /**
     * url 多个地址逗号隔开
     * @return
     */
    String url() default "";

    /**
     * Path prefix to be used by all method-level mappings
     */
    String path() default "";

    /**
     * 默认轮询负载均衡算法
     * @return
     */
    String loadBalancer() default RoundRobinLoadBalancer.NAME;

    /**
     * client 级别的拦截器
     */
    Class<? extends MrcInterceptor> interceptor() default NopeMrcInterceptor.class;

    /**
     * 重试参数定制，默认不开启
     * @return
     */
    MrClientRetryParam retry() default @MrClientRetryParam;

    /**
     * 熔断参数定制，默认不开启
     * @return
     */
    MrClientCircuitBreakerParam circuitBreaker()  default @MrClientCircuitBreakerParam;

}
