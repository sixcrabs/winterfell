package org.winterfell.zinc.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/24
 */
@ConfigurationProperties(prefix = "zinc")
public class ZincProperties {

    /**
     * 接口地址
     */
    private String uri = "http://localhost:4080";

    /**
     * 认证信息
     */
    @NestedConfigurationProperty
    private BasicAuthProperties basicAuth;

    /**
     * Connection timeout.
     */
    private Duration connectionTimeout = Duration.ofSeconds(3);

    /**
     * Read timeout.
     */
    private Duration readTimeout = Duration.ofSeconds(3);

    /**
     * 默认的时间格式 (+8, 北京时)
     */
    private String timestampFormat = "2006-01-02 15:04:05+08:00";

    /**
     * Whether to enable connection requests from multiple execution threads.
     */
    private boolean multiThreaded = true;

    /**
     * shard num, default is 3
     */
    private int shardNum = 3;

    /**
     * 是否开启详细输出
     */
    private boolean debug = false;

    public boolean isDebugEnabled() {
        return debug;
    }

    public ZincProperties setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public int getShardNum() {
        return shardNum;
    }

    public ZincProperties setShardNum(int shardNum) {
        this.shardNum = shardNum;
        return this;
    }

    public boolean isMultiThreaded() {
        return multiThreaded;
    }

    public ZincProperties setMultiThreaded(boolean multiThreaded) {
        this.multiThreaded = multiThreaded;
        return this;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public ZincProperties setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
        return this;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public ZincProperties setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public ZincProperties setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public ZincProperties setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public BasicAuthProperties getBasicAuth() {
        return basicAuth;
    }

    public ZincProperties setBasicAuth(BasicAuthProperties basicAuth) {
        this.basicAuth = basicAuth;
        return this;
    }

    public static class BasicAuthProperties {

        private String username;

        private String password;

        public String getUsername() {
            return username;
        }

        public BasicAuthProperties setUsername(String username) {
            this.username = username;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public BasicAuthProperties setPassword(String password) {
            this.password = password;
            return this;
        }
    }
}
