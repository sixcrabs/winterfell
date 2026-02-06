package org.winterfell.misc.zinc.annotation;

import org.winterfell.misc.zinc.support.ZincFieldTypes;

import java.lang.annotation.*;

import static org.winterfell.misc.zinc.support.ZincConstants.DATETIME_DEFAULT_FORMAT;
import static org.winterfell.misc.zinc.support.ZincConstants.DATETIME_DEFAULT_TZ;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/4/21
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ZincField {

    /**
     * 字段名 默认为空，则采用underline方式获取字段名
     * eg： publish_date
     * @return
     */
    String name() default "";

    /**
     * 字段类型
     *
     * https://zincsearch-docs.zinc.dev/api/index/update-mapping/#request
     *
     * @return
     */
    ZincFieldTypes type() default ZincFieldTypes.text;

    /**
     * Enable index for the field, default is true, it will can't be queried if it disabled.
     * @return
     */
    boolean index() default true;

    /**
     * 是否可以排序，默认 false， numeric and date 类型默认开启
     * @return
     */
    boolean sortable() default false;

    /**
     * 是否支持聚合 默认 false， numeric and date 类型默认开启
     * @return
     */
    boolean aggregatable() default false;

    /**
     * highlight
     * @return
     */
    boolean highlightable() default  false;

    /**
     * 时间字段生效
     *
     * @return
     */
    String dateFormat() default DATETIME_DEFAULT_FORMAT;

    /**
     * 时间字段生效
     *
     * @return
     */
    String dateTimeZone() default DATETIME_DEFAULT_TZ;



}
