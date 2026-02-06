package org.winterfell.misc.remote.mrc.autoconfigure;

import org.winterfell.misc.remote.mrc.MrClient;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * <p>
 * properties for {@link MrClient}
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/5/22
 */
@ConfigurationProperties(prefix = "mrc")
public class MrClientProperties {

    private String nacosServerAddr;

    /**
     * 是否输出请求url到日志中 默认开启
     */
    private Boolean showUrl = true;

    /**
     * 全局断路器配置
     */
    private CircuitBreakerProperties circuitBreaker = CircuitBreakerProperties.of();

    /**
     * 全局限流配置
     */
    private RateLimiterProperties rateLimiter = RateLimiterProperties.of();

    /**
     * 全局重试参数配置
     */
    private RetryProperties retry = RetryProperties.of();


    public RetryProperties getRetry() {
        return retry;
    }

    public MrClientProperties setRetry(RetryProperties retry) {
        this.retry = retry;
        return this;
    }

    public String getNacosServerAddr() {
        return nacosServerAddr;
    }

    public void setNacosServerAddr(String nacosServerAddr) {
        this.nacosServerAddr = nacosServerAddr;
    }

    public Boolean getShowUrl() {
        return showUrl;
    }

    public void setShowUrl(Boolean showUrl) {
        this.showUrl = showUrl;
    }

    public CircuitBreakerProperties getCircuitBreaker() {
        return circuitBreaker;
    }

    public void setCircuitBreaker(CircuitBreakerProperties circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    public RateLimiterProperties getRateLimiter() {
        return rateLimiter;
    }

    public void setRateLimiter(RateLimiterProperties rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    /**
     * 熔断器配置
     */
    public static class CircuitBreakerProperties {

        private boolean enabled;

        private Float failureRateThreshold;

        private Duration waitDurationInOpenState;

        private Integer permittedNumberOfCallsInHalfOpenState;

        private Integer slidingWindowSize;


        public boolean isEnabled() {
            return enabled;
        }

        public CircuitBreakerProperties setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Float getFailureRateThreshold() {
            return failureRateThreshold;
        }

        public CircuitBreakerProperties setFailureRateThreshold(Float failureRateThreshold) {
            this.failureRateThreshold = failureRateThreshold;
            return this;
        }

        public Duration getWaitDurationInOpenState() {
            return waitDurationInOpenState;
        }

        public CircuitBreakerProperties setWaitDurationInOpenState(Duration waitDurationInOpenState) {
            this.waitDurationInOpenState = waitDurationInOpenState;
            return this;
        }

        public Integer getPermittedNumberOfCallsInHalfOpenState() {
            return permittedNumberOfCallsInHalfOpenState;
        }

        public CircuitBreakerProperties setPermittedNumberOfCallsInHalfOpenState(Integer permittedNumberOfCallsInHalfOpenState) {
            this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
            return this;
        }

        public Integer getSlidingWindowSize() {
            return slidingWindowSize;
        }

        public CircuitBreakerProperties setSlidingWindowSize(Integer slidingWindowSize) {
            this.slidingWindowSize = slidingWindowSize;
            return this;
        }

        private CircuitBreakerProperties() {
        }

       public static CircuitBreakerProperties of() {
            // 默认值
            return new CircuitBreakerProperties()
                    .setFailureRateThreshold(50F)
                    .setPermittedNumberOfCallsInHalfOpenState(4)
                    .setWaitDurationInOpenState(Duration.ofSeconds(30))
                    .setSlidingWindowSize(2).setEnabled(true);
        }
    }

    /**
     * 限流配置
     */
    public static class RateLimiterProperties {

        private boolean enabled;

        private Integer limitForPeriod;

        private Duration timeoutDuration;

        private Duration limitRefreshPeriod;

        public Integer getLimitForPeriod() {
            return limitForPeriod;
        }

        public RateLimiterProperties setLimitForPeriod(Integer limitForPeriod) {
            this.limitForPeriod = limitForPeriod;
            return this;
        }

        public Duration getTimeoutDuration() {
            return timeoutDuration;
        }

        public RateLimiterProperties setTimeoutDuration(Duration timeoutDuration) {
            this.timeoutDuration = timeoutDuration;
            return this;
        }

        public Duration getLimitRefreshPeriod() {
            return limitRefreshPeriod;
        }

        public RateLimiterProperties setLimitRefreshPeriod(Duration limitRefreshPeriod) {
            this.limitRefreshPeriod = limitRefreshPeriod;
            return this;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public RateLimiterProperties setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        static RateLimiterProperties of() {
            return new RateLimiterProperties().setEnabled(true)
                    .setLimitRefreshPeriod(Duration.ofSeconds(1))
                    .setLimitForPeriod(10)
                    .setTimeoutDuration(Duration.ofSeconds(10));
        }

    }

    public static class RetryProperties {

        private boolean enabled;

        /**
         * 重试最大尝试次数
         */
        private Integer maxAttempts;

        /**
         * 重试等待的时间
         */
        private Duration waitDuration;

        public Integer getMaxAttempts() {
            return maxAttempts;
        }

        public RetryProperties setMaxAttempts(Integer maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public Duration getWaitDuration() {
            return waitDuration;
        }

        public RetryProperties setWaitDuration(Duration waitDuration) {
            this.waitDuration = waitDuration;
            return this;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public RetryProperties setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        private RetryProperties() {
        }

       public static RetryProperties of() {
            // 默认参数
            return new RetryProperties().setMaxAttempts(3).setWaitDuration(Duration.ofSeconds(3)).setEnabled(true);
        }
    }


}
