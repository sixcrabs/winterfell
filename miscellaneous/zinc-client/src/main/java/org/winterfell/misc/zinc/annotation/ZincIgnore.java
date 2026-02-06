package org.winterfell.misc.zinc.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * 忽略字段
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/4/21
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ZincIgnore {
}
