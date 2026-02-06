package org.winterfell.shared.as.service;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * <p>
 *   lambda工具类
 * </p>
 *
 * @author Alex
 * @since 2025/12/26
 */
public class LambdaUtil {

    /**
     * 获取lambda表达式的实现方法名
     *
     * @param lambda
     * @return
     */
    public static SerializedLambda of(Serializable lambda) {
        if (Objects.isNull(lambda)) {
            throw new NullPointerException("lambda cannot be null!");
        }
        try {
            Method declaredMethod = lambda.getClass().getDeclaredMethod("writeReplace");
            declaredMethod.setAccessible(true);
            return (SerializedLambda) declaredMethod.invoke(lambda);
        } catch (Exception e) {
            throw new RuntimeException("lambda error ", e);
        }
    }
    public static SerializedLambda of(Object lambda) {
        if (Objects.isNull(lambda)) {
            throw new NullPointerException("lambda cannot be null!");
        }
        try {
            Method declaredMethod = lambda.getClass().getDeclaredMethod("writeReplace");
            declaredMethod.setAccessible(true);
            return (SerializedLambda) declaredMethod.invoke(lambda);
        } catch (Exception e) {
            throw new RuntimeException("lambda error ", e);
        }
    }
}