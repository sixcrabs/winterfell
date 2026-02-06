package org.winterfell.misc.srpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * <p>
 * 为客户端生成动态代理对象并实现对 rpc-server 的调用
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/19
 */
public class RpcConsumerFactory {

    public static final Logger logger = LoggerFactory.getLogger(RpcConsumerFactory.class);


    /**
     * access-token
     * 需要和server端一致
     */
    private final String accessToken;

    /**
     * rpc 服务地址
     */
    private final String address;

    /**
     * 接收超时时间 默认 30s
     */
    private int recvTimeout = 30000;

    /**
     * timeout to Poll socket for a reply
     * 请求超时 用于等待服务端长时的方法
     */
    private int requestTimeout = 30000;

    private RpcClient rpcClient;

    private boolean initialized;

    private RpcConsumerFactory(String accessToken, String address) {
        this.accessToken = accessToken;
        this.address = address;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String accessToken;

        /**
         * rpc 服务地址
         */
        private String address;

        /**
         * 接收超时时间 默认 30s
         */
        private int recvTimeout = 30000;

        /**
         * 请求超时时间 默认 30s
         */
        private int requestTimeout = 30000;

        private Builder() {
        }


        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }


        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder recvTimeout(int recvTimeout) {
            this.recvTimeout = recvTimeout;
            return this;
        }

        /**
         * request timeout
         *
         * @param timeout ms
         * @return
         */
        public Builder requestTimeout(int timeout) {
            this.requestTimeout = timeout;
            return this;
        }


        public RpcConsumerFactory build() {
            return new RpcConsumerFactory(this.accessToken, this.address)
                    .setRecvTimeout(this.recvTimeout)
                    .setRequestTimeout(this.requestTimeout);
        }
    }


    /**
     * 初始化 rpc client
     */
    private void init() {
        if (!initialized) {
            this.rpcClient = RpcClient.builder(String.format("tcp://%s", this.address))
                    .recvTimeout(this.recvTimeout)
                    .reqTimeout(this.requestTimeout)
                    .build();
            this.rpcClient.start(new AsyncResultHandler<String>() {
                @Override
                public void complete(String result) {
                    logger.info(result);
                    initialized = true;
                }

                @Override
                public void failed(Throwable error) {
                    logger.error(error.getLocalizedMessage());
                }
            });
        }
    }

    /**
     * 销毁 rpc client 等
     */
    public void destroy() {
        if (rpcClient != null) {
            rpcClient.destroy();
        }
    }

    /**
     * 生成接口类的代理对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject(Class<T> clazz) {
        if (Objects.isNull(clazz)) {
            throw new NullPointerException("`clazz` cannot be null");
        }
        if (!initialized) {
            this.init();
        }
        // Thread.currentThread().getContextClassLoader()
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (proxy, method, args) -> {
            // 组织成 RpcRequest
            // 创建rpc client
            // 接收响应 并反序列化
            // 返回对象
            String className = method.getDeclaringClass().getName();
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();

            if (className.equals(Object.class.getName())) {
                logger.warn("[srpc-client] proxy class-method not support [{}#{}]", className, methodName);
                throw new RpcException("[rpc-client] proxy class-method not support");
            }

            RpcRequest rpcRequest = RpcRequest.builder()
                    .accessToken(accessToken)
                    .targetClassName(className)
                    .targetMethodName(methodName)
                    .parameterTypes(parameterTypes)
                    .parameters(args)
                    .build();

            // 调用 client
            RpcResponse response = rpcClient.call(rpcRequest);
            if (response.getThrowable() != null) {
                // 抛出原始异常
                throw response.getThrowable();
            }
            if (response.getResult() != null) {
                return response.getResult();
            }
            // 记录异常
            logger.error(response.getErrorMsg());
            return null;
        });
    }

    public RpcConsumerFactory setRecvTimeout(int recvTimeout) {
        this.recvTimeout = recvTimeout;
        return this;
    }

    public RpcConsumerFactory setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }
}
