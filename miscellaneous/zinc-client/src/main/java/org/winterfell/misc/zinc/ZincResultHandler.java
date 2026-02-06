package org.winterfell.misc.zinc;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
public interface ZincResultHandler<T> {

    /**
     * 完成回调
     * @param result
     */
    void completed(T result);

    /**
     * 失败回调
     * @param ex
     */
    void failed(Exception ex);
}
