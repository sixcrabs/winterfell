package org.winterfell.misc.timer.cron;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0, 2020/8/6
 */
public final class DateTimeUtil {

    private static final String LOCAL_ZONE_ID = "Asia/Shanghai";

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DateTimeUtil() {
    }

    /**
     * from timecode like
     * @param timeCode  eg: 20200101000000
     * @return
     */
    public static LocalDateTime fromTimeCode(String timeCode) {
        return LocalDateTime.parse(timeCode, DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.CHINA));
    }

    /**
     * now
     *
     * @return
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of(LOCAL_ZONE_ID));
    }

    public static LocalDate nowDate() {
        return LocalDateTime.now(ZoneId.of(LOCAL_ZONE_ID)).toLocalDate();
    }

    /**
     * now string
     *
     * @return
     */
    public static String nowStr() {
        return format(now(), DATE_TIME_PATTERN);
    }

    /**
     * now string with specified pattren
     *
     * @param pattern
     * @return
     */
    public static String nowStr(String pattern) {
        return format(now(), pattern);
    }

    public static String yearStr() {
        return String.valueOf(LocalDate.now().getYear());
    }

    /**
     * is leap year or not
     * @return
     */
    public static boolean isLeapYear() {
        int year = LocalDate.now().getYear();
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * get local date of localdatetime
     *
     * @param dateTime
     * @return
     */
    public static LocalDate getLocalDate(LocalDateTime dateTime) {
        LocalDateTime dt = dateTime == null ? now() : dateTime;
        return LocalDate.ofYearDay(dt.getYear(), dt.getDayOfYear());
    }

    /**
     * 转为 Date
     *
     * @param dateTime
     * @return
     */
    public static Date toDate(LocalDateTime dateTime) {
        LocalDateTime now = dateTime == null ? now() : dateTime;
        Instant instant = now.atZone(ZoneId.of(LOCAL_ZONE_ID)).toInstant();
        return Date.from(instant);
    }

    /**
     * instant
     *
     * @param dateTime
     * @return
     */
    public static Instant toInstant(LocalDateTime dateTime) {
        LocalDateTime now = dateTime == null ? now() : dateTime;
        return now.atZone(ZoneId.of(LOCAL_ZONE_ID)).toInstant();
    }

    /**
     * instant of now
     *
     * @return
     */
    public static Instant toInstant() {
        return toInstant(null);
    }

    /**
     * from date
     *
     * @param date
     * @return
     */
    public static LocalDateTime fromDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.of(LOCAL_ZONE_ID));
    }

    /**
     * format
     *
     * @param dateTime
     * @param pattern
     * @return
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * format
     *
     * @param dateTime
     * @param pattern
     * @return
     */
    public static String format(LocalDate dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * format now to string
     *
     * @param pattern
     * @return
     */
    public static String formatNow(String pattern) {
        return format(now(), pattern);
    }

    /**
     * format with default format pattern
     *
     * @param dateTime
     * @return
     */
    public static String formatDefault(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return dateTime.format(formatter);
    }

    /**
     * from timestamp
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime fromTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.of(LOCAL_ZONE_ID));
    }

    /**
     * 是否是上午
     * @param timestamp
     * @return
     */
    public static boolean isMorning(long timestamp) {
        LocalDateTime localDateTime = fromTimestamp(timestamp);
        return localDateTime.getHour() < 12;
    }

    /**
     * 是否是今天
     *
     * @param dateTime
     * @return
     */
    public static boolean isTodayOrNot(LocalDateTime dateTime) {
        return dateTime.toLocalDate().equals(nowDate());
    }

    /**
     * 和今天相比，是否是往前的一天 或 未来的一天
     *
     * @param dateTime
     * @param offset   左移 或 右移
     * @return
     */
    public static boolean isSomeDayOrNot(LocalDateTime dateTime, int offset) {
        LocalDate nowDate = nowDate();
        if (offset >= 0) {
            return nowDate.plusDays(offset).equals(dateTime.toLocalDate());
        } else {
            return nowDate.minusDays(offset).equals(dateTime.toLocalDate());
        }
    }


    /**
     * from instant
     *
     * @param instant
     * @return
     */
    public static LocalDateTime fromInstant(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.of(LOCAL_ZONE_ID));
    }

    /**
     * parse datetime string with pattern
     *
     * @param dateTime
     * @param pattern
     * @return
     */
    public static LocalDateTime fromStr(String dateTime, String pattern) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern, Locale.CHINA));
    }

    /**
     * parse datetime string
     *
     * @param dateTime
     * @return
     */
    public static LocalDateTime fromStr(String dateTime) {
        return fromStr(dateTime, DATE_TIME_PATTERN);
    }


    /**
     * to timestamp
     *
     * @param localDateTime
     * @return
     */
    public static long toTimestamp(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.of(LOCAL_ZONE_ID)).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * now to timestamp
     *
     * @return
     */
    public static long toTimeStamp() {
        return toTimestamp(now());
    }

    /**
     * to datetime
     *
     * @param time   str of local datetime
     * @param format format str
     * @return
     */
    public static LocalDateTime toDateTime(String time, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format == null ? DATE_TIME_PATTERN : format);
        return LocalDateTime.parse(time, df);
    }

    /**
     * to datetime
     *
     * @param time
     * @return
     */
    public static LocalDateTime toDateTime(String time) {
        return toDateTime(time, null);
    }

    /**
     * str to date
     *
     * @param time
     * @param format
     * @return
     */
    public static Date toDate(String time, String format) {
        return toDate(toDateTime(time, format));
    }

    /**
     * str to date
     *
     * @param time
     * @return
     */
    public static Date toDate(String time) {
        return toDate(toDateTime(time, DATE_TIME_PATTERN));
    }

    /**
     * to map
     *
     * @param dateTime
     * @return
     */
    public static Map toMap(LocalDateTime dateTime) {
        HashMap map = new HashMap(6);
        String minute = dateTime.getMinute() < 10 ? "0" + dateTime.getMinute() : String.valueOf(dateTime.getMinute());
        String second = dateTime.getSecond() < 10 ? "0" + dateTime.getSecond() : String.valueOf(dateTime.getSecond());
        map.put("year", dateTime.getYear());
        map.put("month", dateTime.getMonthValue());
        map.put("day", dateTime.getDayOfMonth());
        map.put("hour", dateTime.getHour());
        map.put("minute", minute);
        map.put("second", second);
        return map;
    }
}
