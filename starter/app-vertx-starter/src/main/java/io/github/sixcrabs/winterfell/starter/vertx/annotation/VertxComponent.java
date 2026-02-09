package io.github.sixcrabs.winterfell.starter.vertx.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * 标记类是个 需要被DI管理的类型
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VertxComponent {
}