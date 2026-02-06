package org.winterfell.misc.srpc;

/**
 * <p>
 * 异常结果handler
 * </p>
 *
 * @author alex
 * @version v1.0 2022/9/4
 */
public interface AsyncResultHandler<T> {

    void complete(T result);

    void failed(Throwable error);
}
