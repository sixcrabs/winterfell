package org.winterfell.starter.javalin.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/26
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JavalinComponentScan {

    /**
     * 扫描路径
     *
     * @return
     */
    String[] value() default {};
}
