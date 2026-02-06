package org.winterfell.shared.as.service;

import java.io.Serializable;

/**
 * <p>
 * 持序列化的双参数函数接口 lambda 需要遵循的格式
 * </p>
 *
 * @author Alex
 * @since 2025/12/26
 */
public interface ServiceBiFunction<T, U, R> extends Serializable {

    /**
     * 函数接口
     * @param t Service实例
     * @param u 参数
     * @return 结果
     */
    R apply(T t, U u);
}