package org.winterfell.misc.srpc;

/**
 * <p>
 * rpc 异常类
 * </p>
 *
 * @author alex
 * @version v1.0 2022/9/4
 */
public class RpcException extends RuntimeException {

    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
