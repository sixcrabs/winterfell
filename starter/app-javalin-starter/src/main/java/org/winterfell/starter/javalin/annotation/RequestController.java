package org.winterfell.starter.javalin.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/7/28
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestController {

    /**
     * 请求根 path
     *
     * @return
     */
    String value() default "";


}
