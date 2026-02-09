package org.winterfell.misc.hutool.mini.collection;

import org.winterfell.misc.hutool.mini.AssertUtil;
import org.winterfell.misc.hutool.mini.Matcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/1/4
 */
public class IterUtil {


    /**
     * Iterator转List<br>
     * 不判断，直接生成新的List
     *
     * @param <E> 元素类型
     * @param iter {@link Iterator}
     * @return List
     * @since 4.0.6
     */
    public static <E> List<E> toList(Iterable<E> iter) {
        return toList(iter.iterator());
    }


    /**
     * Iterator转List<br>
     * 不判断，直接生成新的List
     *
     * @param <E> 元素类型
     * @param iter {@link Iterator}
     * @return List
     * @since 4.0.6
     */
    public static <E> List<E> toList(Iterator<E> iter) {
        final List<E> list = new ArrayList<>();
        while (iter.hasNext()) {
            list.add(iter.next());
        }
        return list;
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterable<?> iterable) {
        return null == iterable || isEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterator<?> Iterator) {
        return null == Iterator || false == Iterator.hasNext();
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterable<?> iterable) {
        return null != iterable && isNotEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterator<?> Iterator) {
        return null != Iterator && Iterator.hasNext();
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iter 被检查的{@link Iterable}对象，如果为{@code null} 返回true
     * @return 是否包含{@code null}元素
     */
    public static boolean hasNull(Iterable<?> iter) {
        return hasNull(null == iter ? null : iter.iterator());
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iter 被检查的{@link Iterator}对象，如果为{@code null} 返回true
     * @return 是否包含{@code null}元素
     */
    public static boolean hasNull(Iterator<?> iter) {
        if (null == iter) {
            return true;
        }
        while (iter.hasNext()) {
            if (null == iter.next()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否全部元素为null
     *
     * @param iter iter 被检查的{@link Iterable}对象，如果为{@code null} 返回true
     * @return 是否全部元素为null
     * @since 3.3.0
     */
    public static boolean isAllNull(Iterable<?> iter) {
        return isAllNull(null == iter ? null : iter.iterator());
    }

    /**
     * 是否全部元素为null
     *
     * @param iter iter 被检查的{@link Iterator}对象，如果为{@code null} 返回true
     * @return 是否全部元素为null
     * @since 3.3.0
     */
    public static boolean isAllNull(Iterator<?> iter) {
        return null == getFirstNoneNull(iter);
    }

    /**
     * 获取集合的第一个非空元素
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @return 第一个非空元素，null表示未找到
     * @since 5.7.2
     */
    public static <T> T getFirstNoneNull(Iterator<T> iterator) {
        return firstMatch(iterator, Objects::nonNull);
    }

    /**
     * 返回{@link Iterator}中第一个匹配规则的值
     *
     * @param <T>      数组元素类型
     * @param iterator {@link Iterator}
     * @param matcher  匹配接口，实现此接口自定义匹配规则
     * @return 匹配元素，如果不存在匹配元素或{@link Iterator}为空，返回 {@code null}
     * @since 5.7.5
     */
    public static <T> T firstMatch(Iterator<T> iterator, Matcher<T> matcher) {
        AssertUtil.notNull(matcher, "Matcher must be not null !");
        if (null != iterator) {
            while (iterator.hasNext()) {
                final T next = iterator.next();
                if (matcher.match(next)) {
                    return next;
                }
            }
        }
        return null;
    }
}
