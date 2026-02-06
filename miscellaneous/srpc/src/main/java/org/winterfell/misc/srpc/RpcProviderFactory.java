package org.winterfell.misc.srpc;

import org.winterfell.misc.srpc.serializer.Serializer;
import org.winterfell.misc.srpc.serializer.SerializerFactory;
import org.winterfell.misc.srpc.serializer.impl.FurySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>
 * 代理服务端处理 rpc 客户端调用并返回
 * <ul>
 *     <li>注册服务、发现服务、服务路由等</li>
 *     <li>启动 tcp 服务, 等待请求</li>
 * </ul>
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/19
 */
public class RpcProviderFactory {

    public static final Logger logger = LoggerFactory.getLogger(RpcProviderFactory.class);

    /**
     * 服务器线程池最大值
     */
    private int maxPoolSize;

    /**
     * 服务器线程池基本值
     */
    private int corePoolSize;

    private int port;

    private String accessToken;

    private ThreadPoolExecutor threadPoolExecutor;

    private Serializer serializer;

    /**
     * 存放注册的服务
     */
    private Map<String, Object> serviceMap = new HashMap<String, Object>();

    /**
     * 服务实例
     */
    private RpcServer rpcServer;

    public RpcProviderFactory() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        /**
         * 服务器线程池最大值
         */
        private int maxPoolSize;

        /**
         * 服务器线程池基本值
         */
        private int corePoolSize;

        private int port;

        private String accessToken;

        private String serializerName = FurySerializer.NAME;

        private Builder() {
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }


        public Builder corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public Builder maxPoolSize(int maxSize) {
            this.maxPoolSize = maxSize;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder serializerName(String serializerName) {
            this.serializerName = serializerName;
            return this;
        }

        public RpcProviderFactory build() {
            return new RpcProviderFactory()
                    .setAccessToken(this.accessToken)
                    .setCorePoolSize(this.corePoolSize)
                    .setMaxPoolSize(this.maxPoolSize)
                    .setSerializer(SerializerFactory.getInstance().getSerializer(serializerName))
                    .setPort(this.port);
        }
    }

    /**
     * 启动 provider
     */
    public void start(AsyncResultHandler resultHandler) {
        init();
        // 实例化 rpc server 并启动
        rpcServer = new RpcServer(this);
        rpcServer.start(new AsyncResultHandler() {
            @Override
            public void complete(Object result) {
                logger.info("[srpc] {}", result.toString());
                if (resultHandler != null) {
                    resultHandler.complete(result);
                }
            }

            @Override
            public void failed(Throwable error) {
                logger.error("[srpc] error: {}", error.getLocalizedMessage());
            }
        });


    }

    /**
     * 停止相关服务等
     */
    public void stop() {
        logger.info("[srpc] stop server...");
    }

    /**
     * 进行初始化 TODO:
     * - 默认参数值设置
     * - 端口有效性检测等
     */
    private void init() {


        // ...

        // ...

        // 初始化线程池提供给 rpc server
        threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                r -> new Thread(r, "rpc-server-thread-" + r.hashCode()),
                (r, executor) -> {
                    throw new RpcException("Rpc server Thread pool is EXHAUSTED!");
                });
    }

    /**
     * 注册 service
     *
     * @param interfaceName
     * @param serviceBean
     */
    public void addService(String interfaceName, Object serviceBean) {
        serviceMap.put(interfaceName, serviceBean);
        logger.info("[srpc] add rpc interface [{}] successfully", interfaceName);
    }

    /**
     * 执行 service
     *
     * @param rpcRequest
     * @return
     */
    public RpcResponse invokeService(RpcRequest rpcRequest) {

        RpcResponse.Builder responseBuilder = RpcResponse.builder();
        Object serviceBean = serviceMap.get(rpcRequest.getTargetClassName());

        if (serviceBean == null) {
            return responseBuilder.errorMsg("The service [" + rpcRequest.getTargetClassName() + "] not found.")
                    .build();
        }

        if (System.currentTimeMillis() - rpcRequest.getCreateAt() > 3 * 60 * 1000) {
            return responseBuilder
                    .errorMsg("The timestamp difference between admin and executor exceeds the limit.")
                    .build();
        }

        try {
            // invoke
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = rpcRequest.getTargetMethodName();
            Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
            Object[] parameters = rpcRequest.getParameters();

            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(serviceBean, parameters);
            responseBuilder.result(result);
        } catch (Throwable t) {
            logger.error("[srpc] provider invokeService error.", t);
            if (t instanceof InvocationTargetException) {
                Throwable internalT = ((InvocationTargetException) t).getTargetException();
                responseBuilder.errorMsg(internalT.getLocalizedMessage());
                responseBuilder.throwable(internalT);
            } else {
                responseBuilder.errorMsg(t.getLocalizedMessage());
                responseBuilder.throwable(t);
            }
        }
        return responseBuilder.build();
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public RpcProviderFactory setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public RpcProviderFactory setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public int getPort() {
        return port;
    }

    public RpcProviderFactory setPort(int port) {
        this.port = port;
        return this;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public RpcProviderFactory setSerializer(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public RpcProviderFactory setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public RpcProviderFactory setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
        return this;
    }

    public Map<String, Object> getServiceMap() {
        return serviceMap;
    }

    public RpcProviderFactory setServiceMap(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
        return this;
    }

    public RpcServer getRpcServer() {
        return rpcServer;
    }

    public RpcProviderFactory setRpcServer(RpcServer rpcServer) {
        this.rpcServer = rpcServer;
        return this;
    }
}
