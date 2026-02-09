package org.winterfell.misc.hutool.mini.func;

import java.io.Serializable;

/**
 * <p>
 * 只有一个参数的函数对象
 * 一个函数接口代表一个函数，用于包装一个函数为对象<br>
 * </p>
 *
 * @author Alex
 * @since 2025/5/9
 */
@FunctionalInterface
public interface FunctionCall<P, R> extends Serializable {

    /**
     * 执行函数
     *
     * @param parameter 参数
     * @return 函数执行结果
     * @throws Exception 自定义异常
     */
    R call(P parameter) throws Exception;

    /**
     * 执行函数，异常包装为RuntimeException
     *
     * @param parameter 参数
     * @return 函数执行结果
     * @since 5.3.6
     */
    default R callWithRuntimeException(P parameter) {
        try {
            return call(parameter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
