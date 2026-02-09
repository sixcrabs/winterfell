package org.winterfell.misc.hutool.mini.convert.impl;

import org.winterfell.misc.hutool.mini.convert.AbstractConverter;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EnumConverter extends AbstractConverter<Object> {

    private Class enumClass;

    /**
     * 构造
     *
     * @param enumClass 转换成的目标Enum类
     */
    public EnumConverter(Class enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    protected Object convertInternal(Object value) {
        return Enum.valueOf(enumClass, convertToStr(value));
    }

    @Override
    public Class getTargetType() {
        return this.enumClass;
    }
}
