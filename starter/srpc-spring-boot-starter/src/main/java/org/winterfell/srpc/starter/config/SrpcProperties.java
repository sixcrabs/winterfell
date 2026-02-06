package org.winterfell.srpc.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * <p>
 * contains `consumer` & `provider`
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/19
 */
@ConfigurationProperties(prefix = "srpc")
public class SrpcProperties {


    /**
     * nacos 注册中心地址; 用于 rpc 服务的自动发现
     * 若配置此参数，则 {@link SrpcProperties#providers} 配置失效
     */
    private String nacosServerAddr;

    /**
     * providers can contain multi
     */
    private List<RpcProvider> providers;

    /**
     * rpc server setting
     */
    private RpcServer server;


    public String getNacosServerAddr() {
        return nacosServerAddr;
    }

    public SrpcProperties setNacosServerAddr(String nacosServerAddr) {
        this.nacosServerAddr = nacosServerAddr;
        return this;
    }

    public List<RpcProvider> getProviders() {
        return providers;
    }

    public SrpcProperties setProviders(List<RpcProvider> providers) {
        this.providers = providers;
        return this;
    }

    public RpcServer getServer() {
        return server;
    }

    public SrpcProperties setServer(RpcServer server) {
        this.server = server;
        return this;
    }

    public static class RpcProvider {

        private String name;

        private String address;

        private String accessToken;

        /**
         * receive timeout millseconds
         */
        private int recvTimeout = 30000;

        /**
         * request timeout ms
         */
        private int requestTimeout = 30000;

        public String getName() {
            return name;
        }

        public RpcProvider setName(String name) {
            this.name = name;
            return this;
        }

        public String getAddress() {
            return address;
        }

        public RpcProvider setAddress(String address) {
            this.address = address;
            return this;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public RpcProvider setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public int getRecvTimeout() {
            return recvTimeout;
        }

        public RpcProvider setRecvTimeout(int recvTimeout) {
            this.recvTimeout = recvTimeout;
            return this;
        }

        public int getRequestTimeout() {
            return requestTimeout;
        }

        public RpcProvider setRequestTimeout(int requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }
    }

    public static class RpcServer {

        private int port;

        private int corePoolSize = 16;

        private int maxPoolSize = 256;

        private String accessToken;

        public int getPort() {
            return port;
        }

        public RpcServer setPort(int port) {
            this.port = port;
            return this;
        }

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public RpcServer setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public RpcServer setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public RpcServer setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }
    }


}
