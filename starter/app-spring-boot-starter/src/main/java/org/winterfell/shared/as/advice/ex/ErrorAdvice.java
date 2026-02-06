package org.winterfell.shared.as.advice.ex;

import java.lang.annotation.*;

/**
 * <p>
 * 用于标记在自定义的 exception 类上
 * </p>
 *
 * @author Alex
 * @since 2025/10/11
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ErrorAdvice {

    /**
     * 错误码. 默认 9999
     * @return
     */
    int code() default 9999;

    /**
     * 异常信息.
     *
     * @return 异常对应的提示信息
     */
    String message() default "Unknown Error!";


}
