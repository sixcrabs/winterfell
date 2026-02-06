package org.winterfell.misc.zinc;

import org.winterfell.misc.zinc.config.idle.IdleConnectionReaper;
import org.winterfell.misc.zinc.http.HttpReapableConnectionManager;
import org.winterfell.misc.zinc.http.ZincHttpClient;
import org.winterfell.misc.zinc.http.ZincHttpClientConfig;
import com.google.gson.Gson;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ProxySelector;
import java.util.Base64;

/**
 * <p>
 * client factory
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
public class ZinClientFactory {

    final static Logger log = LoggerFactory.getLogger(ZinClientFactory.class);

    private ZincHttpClientConfig httpClientConfig;

    /**
     * get client object
     *
     * @return
     */
    public ZinClient getObject() {
        if (httpClientConfig == null) {
            log.debug("[zinc] There is no configuration to create http client. Going to create simple client with default values");
            httpClientConfig = new ZincHttpClientConfig.Builder("http://localhost:4080").build();
        }
        ZincHttpClient client = new ZincHttpClient(httpClientConfig.getServerUri());
        if (httpClientConfig.isDebugEnabled()) {
            client.enableDebug();
        }
        final HttpClientConnectionManager connectionManager = getConnectionManager();
        final NHttpClientConnectionManager asyncConnectionManager = getAsyncConnectionManager();

        client.setHttpClient(createHttpClient(connectionManager));
        client.setAsyncClient(createAsyncHttpClient(asyncConnectionManager));
        // 设置 basic auth
        CredentialsProvider credentialsProvider = httpClientConfig.getCredentialsProvider();
        String password = credentialsProvider.getCredentials(AuthScope.ANY).getPassword();
        String username = credentialsProvider.getCredentials(AuthScope.ANY).getUserPrincipal().getName();
        client.setBasicCredentials("Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));

        // 设置自定义的 gson
        Gson gson = httpClientConfig.getGson();
        if (gson == null) {
            log.info("[zinc] Using default GSON instance");
        } else {
            log.info("[zinc] Using custom GSON instance");
            client.setGson(gson);
        }
        // schedule idle connection reaping if configured
        if (httpClientConfig.getMaxConnectionIdleTime() > 0) {
            log.info("Idle connection reaping enabled...");
            IdleConnectionReaper reaper = new IdleConnectionReaper(httpClientConfig, new HttpReapableConnectionManager(connectionManager, asyncConnectionManager));
            client.setIdleConnectionReaper(reaper);
            reaper.startAsync();
            reaper.awaitRunning();
        } else {
            log.info("Idle connection reaping disabled...");
        }

        return client;
    }

    public ZinClientFactory setHttpClientConfig(ZincHttpClientConfig httpClientConfig) {
        this.httpClientConfig = httpClientConfig;
        return this;
    }

    private CloseableHttpClient createHttpClient(HttpClientConnectionManager connectionManager) {
        return configureHttpClient(
                HttpClients.custom()
                        .setConnectionManager(connectionManager)
                        .setDefaultRequestConfig(getRequestConfig())
                        .setRoutePlanner(getRoutePlanner())
                        .setDefaultCredentialsProvider(httpClientConfig.getCredentialsProvider())
        ).build();
    }

    private CloseableHttpAsyncClient createAsyncHttpClient(NHttpClientConnectionManager connectionManager) {
        return configureHttpClient(
                HttpAsyncClients.custom()
                        .setConnectionManager(connectionManager)
                        .setDefaultRequestConfig(getRequestConfig())
                        .setRoutePlanner(getRoutePlanner())
                        .setDefaultCredentialsProvider(httpClientConfig.getCredentialsProvider())
        ).build();
    }

    private HttpRoutePlanner getRoutePlanner() {
        return new SystemDefaultRoutePlanner(ProxySelector.getDefault());
    }

    /**
     * Extension point
     * <p>
     * Example:
     * </p>
     * <pre>
     * final JestClientFactory factory = new JestClientFactory() {
     *    {@literal @Override}
     *  	protected HttpClientBuilder configureHttpClient(HttpClientBuilder builder) {
     *  		return builder.setDefaultHeaders(...);
     *    }
     * }
     * </pre>
     */
    protected HttpClientBuilder configureHttpClient(final HttpClientBuilder builder) {
        return builder;
    }

    /**
     * Extension point for async client
     */
    protected HttpAsyncClientBuilder configureHttpClient(final HttpAsyncClientBuilder builder) {
        return builder;
    }


    protected RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(httpClientConfig.getConnTimeout())
                .setSocketTimeout(httpClientConfig.getReadTimeout())
                .build();
    }

    protected NHttpClientConnectionManager getAsyncConnectionManager() {
        PoolingNHttpClientConnectionManager retval;

        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setConnectTimeout(httpClientConfig.getConnTimeout())
                .setSoTimeout(httpClientConfig.getReadTimeout())
                .build();

        Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
                .register("http", NoopIOSessionStrategy.INSTANCE)
                .register("https", SSLIOSessionStrategy.getSystemDefaultStrategy())
                .build();

        try {
            retval = new PoolingNHttpClientConnectionManager(
                    new DefaultConnectingIOReactor(ioReactorConfig),
                    sessionStrategyRegistry
            );
        } catch (IOReactorException e) {
            throw new IllegalStateException(e);
        }

        final Integer maxTotal = httpClientConfig.getMaxTotalConnection();
        if (maxTotal != null) {
            retval.setMaxTotal(maxTotal);
        }
        final Integer defaultMaxPerRoute = httpClientConfig.getDefaultMaxTotalConnectionPerRoute();
        if (defaultMaxPerRoute != null) {
            retval.setDefaultMaxPerRoute(defaultMaxPerRoute);
        }
//        final Map<HttpRoute, Integer> maxPerRoute = httpClientConfig.getMaxTotalConnectionPerRoute();
//        for (Map.Entry<HttpRoute, Integer> entry : maxPerRoute.entrySet()) {
//            retval.setMaxPerRoute(entry.getKey(), entry.getValue());
//        }

        return retval;
    }

    // Extension point
    protected HttpClientConnectionManager getConnectionManager() {
        HttpClientConnectionManager retval;

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();

        if (httpClientConfig.isMultiThreaded()) {
            log.info("Using multi thread/connection supporting pooling connection manager");
            final PoolingHttpClientConnectionManager poolingConnMgr = new PoolingHttpClientConnectionManager(registry);

            final Integer maxTotal = httpClientConfig.getMaxTotalConnection();
            if (maxTotal != null) {
                poolingConnMgr.setMaxTotal(maxTotal);
            }
            final Integer defaultMaxPerRoute = httpClientConfig.getDefaultMaxTotalConnectionPerRoute();
            if (defaultMaxPerRoute != null) {
                poolingConnMgr.setDefaultMaxPerRoute(defaultMaxPerRoute);
            }
//            final Map<HttpRoute, Integer> maxPerRoute = httpClientConfig.getMaxTotalConnectionPerRoute();
//            for (Map.Entry<HttpRoute, Integer> entry : maxPerRoute.entrySet()) {
//                poolingConnMgr.setMaxPerRoute(entry.getKey(), entry.getValue());
//            }
            retval = poolingConnMgr;
        } else {
            log.info("Using single thread/connection supporting basic connection manager");
            retval = new BasicHttpClientConnectionManager(registry);
        }

        return retval;
    }

    // Extension point
//    protected HttpClientContext createPreemptiveAuthContext(Set<HttpHost> targetHosts) {
//        HttpClientContext context = HttpClientContext.create();
//        context.setCredentialsProvider(httpClientConfig.getCredentialsProvider());
//        context.setAuthCache(createBasicAuthCache(targetHosts));
//
//        return context;
//    }
//
//    private AuthCache createBasicAuthCache(Set<HttpHost> targetHosts) {
//        AuthCache authCache = new BasicAuthCache();
//        BasicScheme basicAuth = new BasicScheme();
//        for (HttpHost eachTargetHost : targetHosts) {
//            authCache.put(eachTargetHost, basicAuth);
//        }
//
//        return authCache;
//    }
}
