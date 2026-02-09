package org.winterfell.misc.zinc.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * TODO
 * 标记字段为 id 字段
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/4/21
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ZincId {
}
