package org.winterfell.starter.javalin.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * . 定义为一个需要注入的类型
 *  默认都是单例
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/26
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface JavalinComponent {
}
