package org.winterfell.misc.hutool.mini.convert.impl;

import org.winterfell.misc.hutool.mini.DateUtil;
import org.winterfell.misc.hutool.mini.StringUtil;
import org.winterfell.misc.hutool.mini.convert.AbstractConverter;

import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class DateConverter extends AbstractConverter<Date> {
    private Class<? extends java.util.Date> targetType;
    /** 日期格式化 */
    private String format;

    public DateConverter(Class<? extends java.util.Date> targetType) {
        this.targetType = targetType;
    }

    public DateConverter(Class<? extends java.util.Date> targetType, String format) {
        this.targetType = targetType;
        this.format = format;
    }

    /**
     * 获取日期格式
     *
     * @return 设置日期格式
     */
    public String getFormat() {
        return format;
    }

    /**
     * 设置日期格式
     *
     * @param format 日期格式
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * 内部转换器，被 {@link AbstractConverter#convert(Object, Object)} 调用，实现基本转换逻辑<br>
     * 内部转换器转换后如果转换失败可以做如下操作，处理结果都为返回默认值：
     *
     * <pre>
     * 1、返回{@code null}
     * 2、抛出一个{@link RuntimeException}异常
     * </pre>
     *
     * @param value 值
     * @return 转换后的类型
     */
    @Override
    protected Date convertInternal(Object value) {
        long mills = 0;
        // Handle Calendar
        if (value instanceof Calendar) {
            mills = ((Calendar) value).getTimeInMillis();
        }

        // Handle Long
        if (value instanceof Long) {
            // 此处使用自动拆装箱
            mills = (Long) value;
        }

        final String valueStr = convertToStr(value);
        try {
            mills = StringUtil.isBlank(format) ? DateUtil.parse(valueStr).getTime() : DateUtil.parse(valueStr, format).getTime();
        } catch (Exception e) {
            // Ignore Exception
        }

        if (0 == mills) {
            return null;
        }

        // 返回指定类型
        else if (java.util.Date.class == targetType) {
            return new java.util.Date(mills);
        }
        if (java.sql.Date.class == targetType) {
            return new java.sql.Date(mills);
        } else if (java.sql.Time.class == targetType) {
            return new java.sql.Time(mills);
        } else if (java.sql.Timestamp.class == targetType) {
            return new java.sql.Timestamp(mills);
        }

        throw new UnsupportedOperationException(StringUtil.format("Unsupport Date type: {}", this.targetType.getName()));
    }
}
