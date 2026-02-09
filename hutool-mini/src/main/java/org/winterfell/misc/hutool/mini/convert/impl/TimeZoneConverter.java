package org.winterfell.misc.hutool.mini.convert.impl;

import org.winterfell.misc.hutool.mini.convert.AbstractConverter;

import java.util.TimeZone;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class TimeZoneConverter extends AbstractConverter<TimeZone> {

    @Override
    protected TimeZone convertInternal(Object value) {
        return TimeZone.getTimeZone(convertToStr(value));
    }

}
