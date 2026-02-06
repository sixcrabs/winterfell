package org.winterfell.vertx.boot.annotation;

import org.winterfell.vertx.boot.web.RequestMethod;

import java.lang.annotation.*;

/**
 * <p>
 * 请求注解
 * </p>
 *
 * @author alex
 * @version v1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface VertxHttpRequest {

    /**
     * 请求的 path
     *
     * @return
     */
    String value() default "";

    /**
     * if empty will be GET and POST
     * The HTTP request methods to map to, narrowing the primary mapping:
     * GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE.
     * <p><b>Supported at the type level as well as at the method level!</b>
     * When used at the type level, all method-level mappings inherit this
     * HTTP method restriction.
     */
    RequestMethod[] method() default {RequestMethod.GET, RequestMethod.POST};

    /**
     * 是否是 blocking 调用
     *
     * @return
     */
    boolean blocking() default false;
}
