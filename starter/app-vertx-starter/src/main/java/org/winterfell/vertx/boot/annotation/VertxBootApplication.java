package org.winterfell.vertx.boot.annotation;

import com.google.inject.AbstractModule;

import java.lang.annotation.*;

/**
 * <p>
 * 入库注解
 * </p>
 *
 * @author alex
 * @version v1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface VertxBootApplication {


    /**
     * packages to scan
     *
     * @return
     */
    String[] basePackages() default {};

    /**
     * Convenient way to quickly register
     *
     * @return {@code @ConfigurationProperties} annotated beans to register
     */
    Class<?>[] enableProperties() default {};
}
