package org.winterfell.shared.as.support;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * 匹配正则表达式 用于 web dto 参数校验
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/9/21
 */
@Target({FIELD, PARAMETER, METHOD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = RegexFieldValidator.class)
public @interface RegexField {

    /**
     * 不合法提示
     *
     * @return
     */
    String message() default "格式不正确";

    /**
     * 正则表达式值
     *
     * @return
     */
    String regex();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
