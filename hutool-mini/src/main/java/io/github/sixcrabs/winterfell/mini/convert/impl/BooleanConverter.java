package io.github.sixcrabs.winterfell.mini.convert.impl;

import io.github.sixcrabs.winterfell.mini.convert.AbstractConverter;

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