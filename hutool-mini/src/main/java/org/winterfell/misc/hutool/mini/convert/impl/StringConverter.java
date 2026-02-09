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
public class StringConverter extends AbstractConverter<String> {

    @Override
    protected String convertInternal(Object value) {
        return convertToStr(value);
    }

}
