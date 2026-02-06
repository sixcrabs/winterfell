package org.winterfell.shared.as.advice;

import java.lang.annotation.*;

/**
 * <p>
 * 标记注解的 controller 会启用 advice 统一封装返回
 * </p>
 *
 * @author alex
 * @version v1.0 2022/4/6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableRespAdvice {
}
