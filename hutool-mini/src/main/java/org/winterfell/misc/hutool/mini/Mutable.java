package org.winterfell.misc.hutool.mini;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/1/4
 */
public interface Mutable<T> {

    /**
     * 获得原始值
     * @return 原始值
     */
    T get();

    /**
     * 设置值
     * @param value 值
     */
    void set(T value);

}
