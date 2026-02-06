package org.winterfell.misc.timer.cron;

import com.google.common.collect.Lists;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * cron util
 * </p>
 *
 * @author Alex
 * @version v1.0, 2020/8/6
 */
public final class CronUtil {

    private CronUtil() {
    }

    /**
     * 当天内的有效的执行时间集合
     *
     * @param cronExpression
     * @return
     */
    public static List<LocalDateTime> parseWithToday(String cronExpression) throws ParseException {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).minusNanos(1);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return parse(cronExpression, start, end);
    }

    /**
     * 当前时间开始计算的下一次有效的执行时间
     *
     * @param cronExpression
     * @return
     */
    public static LocalDateTime nextValidTime(String cronExpression) throws ParseException {
        CronExpression expression = new CronExpression(cronExpression);
        return DateTimeUtil.fromDate(expression.getNextValidTimeAfter(DateTimeUtil.toDate(DateTimeUtil.now())));
    }

    /**
     * 一段时间内的有效执行时间集合
     *
     * @param cronExpression
     * @param start
     * @param end
     * @return
     */
    public static List<LocalDateTime> parse(String cronExpression, LocalDateTime start, LocalDateTime end) throws ParseException {
        CronExpression expression = new CronExpression(cronExpression);
        List<LocalDateTime> result = Lists.newArrayListWithCapacity(4);
        Date nextValidTime = DateTimeUtil.toDate(start);
        while (DateTimeUtil.fromDate(nextValidTime).isBefore(end)) {
            nextValidTime = expression.getNextValidTimeAfter(nextValidTime);
            if (DateTimeUtil.fromDate(nextValidTime).isBefore(end)) {
                result.add(DateTimeUtil.fromDate(nextValidTime));
            }
        }
        return result;
    }

    /**
     * is valid or not
     *
     * @param cronExpression
     * @return
     */
    public boolean isValidExpression(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }

    /**
     * 秒/分钟/小时/日/月/星期	/年
     */
    public static String localTime2CronExpression(LocalTime time) {
        return join(new String[]{
                String.valueOf(time.getSecond()),
                String.valueOf(time.getMinute()),
                String.valueOf(time.getHour()),
                "*", "*", "?"}, " ", null, null);
    }

    private static <T> String join(T[] array, CharSequence conjunction, String prefix, String suffix) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (T item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            if (isArray(item)) {
                sb.append(join(wrap(item), conjunction, prefix, suffix));
            }
//            else if (item instanceof Iterable<?>) {
//                sb.append(IterUtil.join((Iterable<?>) item, conjunction, prefix, suffix));
//            } else if (item instanceof Iterator<?>) {
//                sb.append(IterUtil.join((Iterator<?>) item, conjunction, prefix, suffix));
//            }
            else {
                sb.append(item);
            }
        }
        return sb.toString();
    }

    private static boolean isArray(Object obj) {
        if (null == obj) {
            // throw new NullPointerException("Object check for isArray is null");
            return false;
        }
        return obj.getClass().isArray();
    }

    private static Object[] wrap(Object obj) {
        if (null == obj) {
            return null;
        }
        if (isArray(obj)) {
            try {
                return (Object[]) obj;
            } catch (Exception e) {
                final String className = obj.getClass().getComponentType().getName();
                switch (className) {
                    case "long":
                        return wrap((long[]) obj);
                    case "int":
                        return wrap((int[]) obj);
                    case "short":
                        return wrap((short[]) obj);
                    case "char":
                        return wrap((char[]) obj);
                    case "byte":
                        return wrap((byte[]) obj);
                    case "boolean":
                        return wrap((boolean[]) obj);
                    case "float":
                        return wrap((float[]) obj);
                    case "double":
                        return wrap((double[]) obj);
                    default:
                        return null;
                }
            }
        }
        throw new RuntimeException(String.format("[%s] is not Array!", obj.getClass().getSimpleName()));
    }
}
