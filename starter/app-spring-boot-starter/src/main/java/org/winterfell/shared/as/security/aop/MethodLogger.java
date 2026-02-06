package org.winterfell.shared.as.security.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 注解方法(controller/service/...)，用于记录方法调用日志
 * </p>
 *
 * @author Alex
 * @since 2025/10/14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodLogger {

    // 模块 默认空
    String module() default "";
}
