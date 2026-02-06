package org.winterfell.vertx.boot;

import static io.vertx.core.VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE;
import static io.vertx.core.VertxOptions.DEFAULT_WORKER_POOL_SIZE;

/**
 * <p>
 * vertx 应用属性配置
 * </p>
 *
 * @author alex
 * @version v1.0
 */
public class VertxProperties {

    private int eventLoopPoolSize = DEFAULT_EVENT_LOOP_POOL_SIZE;

    private int workerPoolSize = DEFAULT_WORKER_POOL_SIZE;

    /**
     * MaxWorkerExecuteTime 默认 120s
     */
    private int maxWorkerExecuteTimeInSeconds = 120;

    /**
     * 应用配置项
     */
    private ApplicationProperty application;

    /**
     * http server 配置
     */
    private Server server;


    public int getMaxWorkerExecuteTimeInSeconds() {
        return maxWorkerExecuteTimeInSeconds;
    }

    public VertxProperties setMaxWorkerExecuteTimeInSeconds(int maxWorkerExecuteTimeInSeconds) {
        this.maxWorkerExecuteTimeInSeconds = maxWorkerExecuteTimeInSeconds;
        return this;
    }

    public int getEventLoopPoolSize() {
        return eventLoopPoolSize;
    }

    public VertxProperties setEventLoopPoolSize(int eventLoopPoolSize) {
        this.eventLoopPoolSize = eventLoopPoolSize;
        return this;
    }

    public int getWorkerPoolSize() {
        return workerPoolSize;
    }

    public VertxProperties setWorkerPoolSize(int workerPoolSize) {
        this.workerPoolSize = workerPoolSize;
        return this;
    }

    public ApplicationProperty getApplication() {
        return application;
    }

    public VertxProperties setApplication(ApplicationProperty application) {
        this.application = application;
        return this;
    }

    public Server getServer() {
        return server;
    }

    public VertxProperties setServer(Server server) {
        this.server = server;
        return this;
    }

    public static class ApplicationProperty {

        private String name;

        public String getName() {
            return name;
        }

        public ApplicationProperty setName(String name) {
            this.name = name;
            return this;
        }
    }

    public static class Server {

        private Integer port;

        private String host;

        /**
         * 启动的 http 实例数
         */
        private Integer instances = 1;

        public Integer getInstances() {
            return instances;
        }

        public Server setInstances(Integer instances) {
            this.instances = instances;
            return this;
        }

        public Integer getPort() {
            return port;
        }

        public Server setPort(Integer port) {
            this.port = port;
            return this;
        }

        public String getHost() {
            return host;
        }

        public Server setHost(String host) {
            this.host = host;
            return this;
        }
    }

}
