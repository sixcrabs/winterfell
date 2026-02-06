package org.winterfell.misc.hutool.mini;

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
public class DateUtil {


    /**
     * 转换为Calendar对象
     *
     * @param date 日期对象
     * @return Calendar对象
     */
    public static Calendar calendar(Date date) {
        return calendar(date.getTime());
    }

    /**
     * 转换为Calendar对象
     *
     * @param millis 时间戳
     * @return Calendar对象
     */
    public static Calendar calendar(long millis) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal;
    }

    /**
     * FIXME:
     * TODO:
     * @param dateStr
     * @return
     */
    public static Date parse(String dateStr) {
        if (null == dateStr) {
            return null;
        }
        // 去掉两边空格并去掉中文日期中的“日”，以规范长度
        dateStr = dateStr.trim().replace("日", "");
        int length = dateStr.length();
        if (NumberUtil.isNumber(dateStr)) {
            // 纯数字形式
            if (length == DatePattern.PURE_DATETIME_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATETIME_PATTERN);
            } else if (length == DatePattern.PURE_DATETIME_MS_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATETIME_MS_PATTERN);
            } else if (length == DatePattern.PURE_DATE_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATE_PATTERN);
            } else if (length == DatePattern.PURE_TIME_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_TIME_PATTERN);
            }
        }

        return null;
    }

    /**
     * FIXME:
     * TODO:
     * @param dateStr
     * @param format
     * @return
     */
    public static Date parse(String dateStr, String format) {
        return null;
    }
}
