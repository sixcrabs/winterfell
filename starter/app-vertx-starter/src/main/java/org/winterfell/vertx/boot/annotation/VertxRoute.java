package org.winterfell.vertx.boot.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * vertx 路由注解
 * </p>
 *
 * @author alex
 * @version v1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface VertxRoute {

    /**
     * 路由path 默认空即 '/'
     *
     * @return
     */
    String value() default "";

    /**
     * if true, with executeBlockingHandler()
     * @return
     */
    boolean blocking() default false;
}
