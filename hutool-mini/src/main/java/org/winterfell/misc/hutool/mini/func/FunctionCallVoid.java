package org.winterfell.misc.hutool.mini.func;

import java.io.Serializable;

/**
 * <p>
 * 一个函数接口代表一个函数，用于包装一个函数为对象
 * 无返回对象
 * </p>
 *
 * @author Alex
 * @since 2025/5/9
 */
@FunctionalInterface
public interface FunctionCallVoid<P> extends Serializable {

    /**
     * 执行函数
     *
     * @param parameters 参数列表
     * @throws Exception 自定义异常
     */
    @SuppressWarnings("unchecked")
    void call(P... parameters) throws Exception;

    /**
     * 执行函数，异常包装为RuntimeException
     *
     * @param parameters 参数列表
     */
    @SuppressWarnings("unchecked")
    default void callWithRuntimeException(P... parameters) {
        try {
            call(parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
