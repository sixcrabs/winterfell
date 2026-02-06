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
public class BooleanConverter extends AbstractConverter<Boolean> {

    @Override
    protected Boolean convertInternal(Object value) {
        String valueStr = convertToStr(value);
        return PrimitiveConverter.parseBoolean(valueStr);
    }

}
