package org.winterfell.misc.zinc.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * .标记pojo类为 zinc 索引
 *
 *
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/4/21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZincIndex {


    /**
     * 索引名称
     * 默认空 会使用对应数据对象进行名称创建
     *
     * @return
     */
    String name() default "";


    /**
     * 名称前缀 用于区分索引
     * eg： my_idx1, other_idx1
     *
     * @return
     */
    String prefix() default "";


    /**
     * 分片数 默认 3
     * 建议分片数小于或等于 cpu 核心数，如果设置大于核心数，会被矫正为核心数
     *
     * @return
     */
    int numberOfShards() default 3;
}
