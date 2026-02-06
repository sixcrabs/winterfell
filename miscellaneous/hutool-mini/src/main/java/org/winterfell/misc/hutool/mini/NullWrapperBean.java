package org.winterfell.misc.hutool.mini;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/1/4
 */
public class NullWrapperBean<T> {

    private final Class<T> clazz;

    /**
     * @param clazz null的类型
     */
    public NullWrapperBean(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 获取null值对应的类型
     *
     * @return 类型
     */
    public Class<T> getWrappedClass() {
        return clazz;
    }
}
