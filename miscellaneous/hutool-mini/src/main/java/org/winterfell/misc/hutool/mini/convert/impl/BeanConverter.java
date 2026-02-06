package org.winterfell.misc.hutool.mini.convert.impl;

import org.winterfell.misc.hutool.mini.*;
import org.winterfell.misc.hutool.mini.convert.AbstractConverter;

import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class BeanConverter<T> extends AbstractConverter<T> {

    private Class<T> beanClass;
    private CopyOptions copyOptions;

    /**
     * 构造，默认转换选项，注入失败的字段忽略
     *
     * @param beanClass 转换成的目标Bean类
     */
    public BeanConverter(Class<T> beanClass) {
        this(beanClass, CopyOptions.create().setIgnoreError(true));
        this.beanClass = beanClass;
    }

    /**
     * 构造
     *
     * @param beanClass 转换成的目标Bean类
     * @param copyOptions Bean转换选项参数
     */
    public BeanConverter(Class<T> beanClass, CopyOptions copyOptions) {
        this.beanClass = beanClass;
        this.copyOptions = copyOptions;
    }

    @Override
    protected T convertInternal(Object value) {
        if(value instanceof Map || value instanceof ValueProvider || BeanUtil.isBean(value.getClass())) {
            //限定被转换对象类型
            return BeanCopier.create(value, ReflectUtil.newInstanceIfPossible(this.beanClass), copyOptions).copy();
        }
        return null;
    }

    @Override
    public Class<T> getTargetType() {
        return this.beanClass;
    }
}
