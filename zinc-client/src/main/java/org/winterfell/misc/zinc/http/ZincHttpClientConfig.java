package org.winterfell.misc.zinc.http;

import org.winterfell.misc.zinc.config.ZinClientConfig;
import com.google.gson.Gson;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
public class ZincHttpClientConfig extends ZinClientConfig {

    private final CredentialsProvider credentialsProvider;

    private final Integer maxTotalConnection;
    private final Integer defaultMaxTotalConnectionPerRoute;
    private final String serverUri;


    private ZincHttpClientConfig(Builder builder) {
        this.serverUri = builder.serverUri;
        this.gson = builder.gson;
        this.isMultiThreaded = builder.isMultiThreaded;
        this.connTimeout = builder.connTimeout;
        this.readTimeout = builder.readTimeout;
        this.maxConnectionIdleTime = builder.maxConnectionIdleTime;
        this.maxConnectionIdleTimeDurationTimeUnit = builder.maxConnectionIdleTimeDurationTimeUnit;
        this.credentialsProvider = builder.credentialsProvider;
        this.maxTotalConnection = builder.maxTotalConnection;
        this.defaultMaxTotalConnectionPerRoute = builder.defaultMaxTotalConnectionPerRoute;
        this.debugEnabled = builder.debug;
    }

    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public Integer getMaxTotalConnection() {
        return maxTotalConnection;
    }

    public Integer getDefaultMaxTotalConnectionPerRoute() {
        return defaultMaxTotalConnectionPerRoute;
    }

    public String getServerUri() {
        return serverUri;
    }

    public static class Builder {

        /**
         * 接口地址
         */
        private final String serverUri;

        /**
         * 序列化
         */
        private Gson gson;

        private boolean isMultiThreaded;

        protected long maxConnectionIdleTime = -1L;

        private Integer connTimeout = 3000;

        private Integer readTimeout = 3000;

        private TimeUnit maxConnectionIdleTimeDurationTimeUnit = TimeUnit.SECONDS;

        private CredentialsProvider credentialsProvider;

        private Integer maxTotalConnection;

        private Integer defaultMaxTotalConnectionPerRoute;

        private boolean debug = false;


        public Builder(String serverUri) {
            this.serverUri = serverUri;
        }

        public Builder gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        public Builder multiThreaded(boolean isMultiThreaded) {
            this.isMultiThreaded = isMultiThreaded;
            return this;
        }

        public Builder connTimeout(int connTimeout) {
            this.connTimeout = connTimeout;
            return this;
        }

        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder enableDebugOrNot(boolean debugEnable) {
            this.debug = debugEnable;
            return this;
        }

        /**
         * Set a custom instance of an implementation of <code>CredentialsProvider</code>.
         * This method will override any previous credential setting (including <code>defaultCredentials</code>) on this builder instance.
         */
        public Builder credentialsProvider(CredentialsProvider credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        /**
         * 默认的 basic auth
         *
         * @param username
         * @param password
         * @return
         */
        public Builder defaultCredentials(String username, String password) {
            this.credentialsProvider = new BasicCredentialsProvider();
            this.credentialsProvider.setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password)
            );
            return this;
        }

        public Builder maxTotalConnection(Integer maxTotalConnection) {
            this.maxTotalConnection = maxTotalConnection;
            return this;
        }

        public Builder defaultMaxTotalConnectionPerRoute(Integer defaultMaxTotalConnectionPerRoute) {
            this.defaultMaxTotalConnectionPerRoute = defaultMaxTotalConnectionPerRoute;
            return this;
        }

        public ZincHttpClientConfig build() {
            return new ZincHttpClientConfig(this);
        }


    }
}
