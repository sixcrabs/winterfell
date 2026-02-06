package org.winterfell.misc.hutool.mini.convert.impl;

import org.winterfell.misc.hutool.mini.ClassUtil;
import org.winterfell.misc.hutool.mini.convert.AbstractConverter;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class ClassConverter extends AbstractConverter<Class<?>> {

    @Override
    protected Class<?> convertInternal(Object value) {
        String valueStr = convertToStr(value);
        try {
            return ClassUtil.getClassLoader().loadClass(valueStr);
        } catch (Exception e) {
            // Ignore Exception
        }
        return null;
    }

}
