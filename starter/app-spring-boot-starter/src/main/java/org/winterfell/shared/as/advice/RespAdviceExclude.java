package org.winterfell.shared.as.advice;

import java.lang.annotation.*;

/**
 * <p>
 * 排除、不使用默认的 respAdvice
 * - 标记controller方法
 * - 标记返回类型
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/8/15
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RespAdviceExclude {
}
