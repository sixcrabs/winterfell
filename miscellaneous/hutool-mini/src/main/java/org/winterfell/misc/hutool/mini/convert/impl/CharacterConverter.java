package org.winterfell.misc.hutool.mini.convert.impl;

import org.winterfell.misc.hutool.mini.StringUtil;
import org.winterfell.misc.hutool.mini.convert.AbstractConverter;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class CharacterConverter extends AbstractConverter<Character> {

    @Override
    protected Character convertInternal(Object value) {
        if(char.class == value.getClass()){
            return Character.valueOf((char)value);
        }else{
            final String valueStr = convertToStr(value);
            if (StringUtil.isNotBlank(valueStr)) {
                return Character.valueOf(valueStr.charAt(0));
            }
        }
        return null;
    }

}
