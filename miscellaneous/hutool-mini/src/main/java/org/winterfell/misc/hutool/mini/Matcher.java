package org.winterfell.misc.hutool.mini;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/1/4
 */
@FunctionalInterface
public interface Matcher<T> {
    /**
     * 给定对象是否匹配
     *
     * @param t 对象
     * @return 是否匹配
     */
    boolean match(T t);
}
