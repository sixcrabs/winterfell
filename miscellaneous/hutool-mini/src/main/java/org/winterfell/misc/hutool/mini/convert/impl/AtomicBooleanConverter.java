package org.winterfell.misc.hutool.mini.convert.impl;

import org.winterfell.misc.hutool.mini.convert.AbstractConverter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class AtomicBooleanConverter extends AbstractConverter<AtomicBoolean> {

    @Override
    protected AtomicBoolean convertInternal(Object value) {
        if (boolean.class == value.getClass()) {
            return new AtomicBoolean((boolean) value);
        }
        if (value instanceof Boolean) {
            return new AtomicBoolean((Boolean) value);
        }
        final String valueStr = convertToStr(value);
        return new AtomicBoolean(PrimitiveConverter.parseBoolean(valueStr));
    }

}
