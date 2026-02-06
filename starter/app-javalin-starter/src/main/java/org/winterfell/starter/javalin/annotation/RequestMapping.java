package org.winterfell.starter.javalin.annotation;

import io.javalin.http.HandlerType;
import io.javalin.plugin.openapi.annotations.HttpMethod;

import java.lang.annotation.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/7/28
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RequestMapping {

    /**
     * 请求 path
     * @return
     */
    String value();

    HandlerType method() default HandlerType.GET;
}
