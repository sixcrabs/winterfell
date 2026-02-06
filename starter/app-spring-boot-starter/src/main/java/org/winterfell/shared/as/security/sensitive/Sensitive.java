package org.winterfell.shared.as.security.sensitive;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * <p>
 * 标记 pojo 为敏感数据 进行数据脱敏
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/8/2
 */
//@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerializer.class)
@Inherited
public @interface Sensitive {


    /**
     * 类型,默认为空
     *
     * @return
     */
    SensitiveType type() default SensitiveType.EMPTY;

    /**
     * 匹配的正则表达式
     *
     * @return 格式
     */
    String pattern() default "";

    /**
     * 正则表达式的第几个哪些分组; 这些分组将被替换为掩码mask
     * 默认第一个分组
     *
     * @return
     */
    int[] group() default 1;

    /**
     * 替换的字符串
     *
     * @return
     */
    String mask() default "*";


}
