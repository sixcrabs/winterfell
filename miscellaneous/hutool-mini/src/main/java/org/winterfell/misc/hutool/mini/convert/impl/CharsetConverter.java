package org.winterfell.misc.hutool.mini.convert.impl;

import org.winterfell.misc.hutool.mini.CharsetUtil;
import org.winterfell.misc.hutool.mini.convert.AbstractConverter;

import java.nio.charset.Charset;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class CharsetConverter extends AbstractConverter<Charset> {

    @Override
    protected Charset convertInternal(Object value) {
        return CharsetUtil.charset(convertToStr(value));
    }

}
