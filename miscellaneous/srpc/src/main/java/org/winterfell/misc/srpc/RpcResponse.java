package org.winterfell.misc.srpc;


import java.io.Serializable;

/**
 * <p>
 * 请求响应体
 * <ul>
 *     <li>封装实际结果</li>
 *     <li>封装错误信息等</li>
 * </ul>
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/19
 */
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = -295799950886562067L;

    /**
     * 关联到请求
     */
    private final String requestId;

    /**
     * 调用异常
     */
    private String errorMsg;

    /**
     * 正常调用结果
     */
    private Object result;

    /**
     * 方法返回的异常
     */
    private Throwable throwable;

    private RpcResponse(String requestId) {
        this.requestId = requestId;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String errorMsg;

        private String requestId;

        private Object result;

        private Throwable throwable;

        private Builder() {
        }

        public Builder errorMsg(String msg) {
            this.errorMsg = msg;
            return this;
        }

        public Builder requestId(String id) {
            this.requestId = id;
            return this;
        }

        public Builder result(Object result) {
            this.result = result;
            return this;
        }

        public Builder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public RpcResponse build() {
            return new RpcResponse(this.requestId)
                    .setResult(this.result)
                    .setThrowable(this.throwable)
                    .setErrorMsg(this.errorMsg);
        }
    }

    public RpcResponse setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public RpcResponse setResult(Object result) {
        this.result = result;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Object getResult() {
        return result;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public RpcResponse setThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }
}
