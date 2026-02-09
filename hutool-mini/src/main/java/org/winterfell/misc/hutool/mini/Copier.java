package org.winterfell.misc.hutool.mini;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public interface Copier<T> {
    /**
     * 执行拷贝
     * @return 拷贝的目标
     */
    T copy();
}
