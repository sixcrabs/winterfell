package org.winterfell.misc.remote.mrc.support;

import org.winterfell.misc.remote.mrc.adapter.CircuitBreakerCallAdapter;
import org.winterfell.misc.remote.mrc.adapter.ExecuteCallAdapter;
import org.winterfell.misc.remote.mrc.adapter.RateLimiterCallAdapter;
import org.winterfell.misc.remote.mrc.adapter.RetryCallAdapter;
import org.winterfell.misc.remote.mrc.autoconfigure.MrClientProperties;
import org.winterfell.misc.remote.mrc.interceptor.MrcInterceptor;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

/**
 * <p>
 * context for client
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/4/13
 */
public class MrClientContext {

    public static final Logger log = LoggerFactory.getLogger(MrClientContext.class);

    private final OkHttpClient httpClient;

    private final MrClientProperties properties;

    private ConcurrentHashMap<String, Retrofit> services = new ConcurrentHashMap<>(2);

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public ConcurrentMap<String, Retrofit> getServices() {
        return services;
    }

    /**
     * Create a CircuitBreaker with custom config
     * see: https://resilience4j.readme.io/docs/circuitbreaker
     */
    private final CircuitBreaker circuitBreaker;

    /**
     * default ratelimit config
     * see: https://resilience4j.readme.io/docs/ratelimiter
     */
    private final RateLimiter rateLimiter;

    /**
     * see: https://resilience4j.readme.io/docs/retry
     */
    private final Retry retry;

    public MrClientContext(OkHttpClient httpClient, MrClientProperties properties) {
        this.httpClient = httpClient;
        this.properties = properties;

        MrClientProperties.CircuitBreakerProperties circuitBreakerProperties = properties.getCircuitBreaker();
        MrClientProperties.RateLimiterProperties rateLimiterProperties = properties.getRateLimiter();
        MrClientProperties.RetryProperties retryProperties = properties.getRetry();

        this.circuitBreaker = circuitBreakerProperties.isEnabled() ? CircuitBreaker.of("GlobalBreaker", CircuitBreakerConfig.custom()
                .failureRateThreshold(circuitBreakerProperties.getFailureRateThreshold())
                .slowCallDurationThreshold(Duration.ofSeconds(30))
                .waitDurationInOpenState(circuitBreakerProperties.getWaitDurationInOpenState())
                .permittedNumberOfCallsInHalfOpenState(circuitBreakerProperties.getPermittedNumberOfCallsInHalfOpenState())
                .slidingWindowSize(circuitBreakerProperties.getSlidingWindowSize())
                .minimumNumberOfCalls(6)
                .recordExceptions(IOException.class, TimeoutException.class)
                .build()) : null;

        this.rateLimiter = rateLimiterProperties.isEnabled() ? RateLimiterRegistry.of(RateLimiterConfig.custom()
                .limitRefreshPeriod(rateLimiterProperties.getLimitRefreshPeriod())
                .limitForPeriod(rateLimiterProperties.getLimitForPeriod())
                .timeoutDuration(rateLimiterProperties.getTimeoutDuration())
                .build()).rateLimiter("GlobalRateLimiter") : null;

        this.retry = retryProperties.isEnabled() ? Retry.of("GlobalRetry", RetryConfig.custom()
                .maxAttempts(retryProperties.getMaxAttempts())
                .waitDuration(retryProperties.getWaitDuration())
                .retryExceptions(IOException.class, TimeoutException.class)
//                .ignoreExceptions(BusinessException.class, OtherBusinessException.class)
                .build()) : null;

    }

    /**
     * 添加一个 client service
     *
     * @param serviceId
     * @param baseUrl
     * @param interceptor
     * @return
     */
    public Retrofit addService(String serviceId, String baseUrl, MrcInterceptor interceptor,
                               CircuitBreaker circuitBreaker, Retry retry) {
        if (!services.containsKey(serviceId)) {
            services.putIfAbsent(serviceId, createRetrofit(baseUrl, interceptor, circuitBreaker, retry));
        }
        return services.get(serviceId);
    }

    /**
     * create a {@link Retrofit}
     *
     * @param baseUrl
     * @return
     */
    private Retrofit createRetrofit(String baseUrl, MrcInterceptor interceptor,
                                    CircuitBreaker circuitBreakerCustomed, Retry retryCustomed) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create());
        // 从全局配置里复制builder出来
        OkHttpClient.Builder clientBuilder = this.httpClient.newBuilder();
        if (interceptor != null && !interceptor.shouldSkip()) {
            clientBuilder.addInterceptor(interceptor.nativeInterceptor());
        }
        builder.client(clientBuilder.build());
        if (rateLimiter != null) {
            builder.addCallAdapterFactory(RateLimiterCallAdapter.of(rateLimiter));
        }
        if (circuitBreakerCustomed != null) {
            builder.addCallAdapterFactory(CircuitBreakerCallAdapter.of(circuitBreakerCustomed));
        } else {
            if (circuitBreaker != null) {
                builder.addCallAdapterFactory(CircuitBreakerCallAdapter.of(circuitBreaker));
            }
        }
        if (retryCustomed != null) {
            builder.addCallAdapterFactory(RetryCallAdapter.of(retryCustomed));
        } else {
            if (retry != null) {
                builder.addCallAdapterFactory(RetryCallAdapter.of(retry));
            }
        }
        builder.addCallAdapterFactory(new ExecuteCallAdapter());
        return builder.build();

    }
}
