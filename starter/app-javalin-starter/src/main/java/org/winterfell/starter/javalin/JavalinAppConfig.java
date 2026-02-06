package org.winterfell.starter.javalin;

import org.winterfell.starter.javalin.annotation.JavalinProperties;
import io.swagger.v3.oas.models.info.Info;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static org.winterfell.starter.javalin.JavalinAppConfig.PREFIX;

/**
 * <p>
 * javalin app 配置
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/26
 */
@JavalinProperties(prefix = PREFIX)
public class JavalinAppConfig implements Serializable {

    public static final String PREFIX = "javalin";

    /**
     * 服务信息
     */
    private ServerProperties server;

    /**
     * 应用信息
     */
    private ApplicationProperties application;

    /**
     * openapi 配置项
     */
    private OpenApiProperties openapi;

    public OpenApiProperties getOpenapi() {
        return openapi;
    }

    public JavalinAppConfig setOpenapi(OpenApiProperties openapi) {
        this.openapi = openapi;
        return this;
    }

    public ServerProperties getServer() {
        return server;
    }

    public JavalinAppConfig setServer(ServerProperties server) {
        this.server = server;
        return this;
    }

    public ApplicationProperties getApplication() {
        return application;
    }

    public JavalinAppConfig setApplication(ApplicationProperties application) {
        this.application = application;
        return this;
    }

    public static class OpenApiProperties {

        /**
         * 是否启用 openapi 默认开启
         */
        private boolean enabled = true;

        /**
         * info 信息
         */
        private Info info;

        /**
         * 注解扫描的包路径
         */
        private List<String> scanPackages = Collections.singletonList("cn.piesat.*");

        /**
         * swagger
         */
        private PathAndTitleProperties swagger = new PathAndTitleProperties("/swagger-ui");

//        private PathAndTitleProperties redoc = new PathAndTitleProperties("/redoc");

        /**
         * 忽略的path
         */
        private List<String> ignorePaths = Collections.emptyList();

        /**
         * 包含的path
         */
        private List<String> includePaths = Collections.emptyList();

        /**
         * disable cache
         */
        private boolean disableCache;

        public boolean isEnabled() {
            return enabled;
        }

        public OpenApiProperties setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Info getInfo() {
            return info;
        }

        public OpenApiProperties setInfo(Info info) {
            this.info = info;
            return this;
        }

        public List<String> getScanPackages() {
            return scanPackages;
        }

        public OpenApiProperties setScanPackages(List<String> scanPackages) {
            this.scanPackages = scanPackages;
            return this;
        }

        public PathAndTitleProperties getSwagger() {
            return swagger;
        }

        public OpenApiProperties setSwagger(PathAndTitleProperties swagger) {
            this.swagger = swagger;
            return this;
        }

        public List<String> getIgnorePaths() {
            return ignorePaths;
        }

        public OpenApiProperties setIgnorePaths(List<String> ignorePaths) {
            this.ignorePaths = ignorePaths;
            return this;
        }

        public List<String> getIncludePaths() {
            return includePaths;
        }

        public OpenApiProperties setIncludePaths(List<String> includePaths) {
            this.includePaths = includePaths;
            return this;
        }

        public boolean isDisableCache() {
            return disableCache;
        }

        public OpenApiProperties setDisableCache(boolean disableCache) {
            this.disableCache = disableCache;
            return this;
        }

        public static class PathAndTitleProperties {

            private final String path;

            private String title = "";

            public PathAndTitleProperties(String path) {
                this.path = path;
            }


            public String getPath() {
                return path;
            }

            public String getTitle() {
                return title;
            }

            public PathAndTitleProperties setTitle(String title) {
                this.title = title;
                return this;
            }
        }

    }

    public static class ApplicationProperties {

        private String name;

        public String getName() {
            return name;
        }

        public ApplicationProperties setName(String name) {
            this.name = name;
            return this;
        }
    }


    public static class ServerProperties {

        private String host;

        private Integer port;


        public String getHost() {
            return host;
        }

        public ServerProperties setHost(String host) {
            this.host = host;
            return this;
        }

        public Integer getPort() {
            return port;
        }

        public ServerProperties setPort(Integer port) {
            this.port = port;
            return this;
        }
    }
}
