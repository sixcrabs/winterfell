package io.github.sixcrabs.winterfell.mini.convert.impl;

import io.github.sixcrabs.winterfell.mini.convert.AbstractConverter;

import java.util.UUID;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class UUIDConverter extends AbstractConverter<UUID> {

    @Override
    protected UUID convertInternal(Object value) {
        return UUID.fromString(convertToStr(value));
    }

}