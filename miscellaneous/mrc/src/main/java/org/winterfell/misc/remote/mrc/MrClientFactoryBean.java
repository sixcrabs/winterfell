package org.winterfell.misc.remote.mrc;

import org.winterfell.misc.remote.mrc.autoconfigure.MrClientProperties;
import org.winterfell.misc.remote.mrc.interceptor.MrcInterceptor;
import org.winterfell.misc.remote.mrc.loadbalance.LoadBalancer;
import org.winterfell.misc.remote.mrc.support.MrClientContext;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import retrofit2.Retrofit;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.winterfell.misc.remote.mrc.support.MrConstants.*;

/**
 * <p>
 * factory bean 创建出 mrclient bean
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/4/13
 */
public class MrClientFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {


    private static final Logger logger = LoggerFactory.getLogger(MrClientFactoryBean.class);

    public static final String PROPERTY_TYPE = "type";
    public static final String PROPERTY_URL = "url";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_PATH = "path";
    public static final String PROPERTY_INTERCEPTOR = "interceptorClazz";
    public static final String PROPERTY_RETRY = "retryProperties";
    public static final String PROPERTY_CIRCUIT_BREAKER = "circuitBreakerProperties";
    public static final String PROPERTY_LOAD_BALANCER = "loadBalancer";


    /**
     * type class
     */
    private Class<?> type;

    /**
     * 名称
     */
    private String name;

    /**
     * url
     */
    private String url;

    /**
     * root path eg: /api
     */
    private String path;

    /**
     * interceptor class for MrClient
     */
    private Class<? extends MrcInterceptor> interceptorClazz;

    private MrClientProperties.CircuitBreakerProperties circuitBreakerProperties;

    private MrClientProperties.RetryProperties retryProperties;

    // 用于配置了多个服务地址时 负载
    private LoadBalancer loadBalancer;

    private ApplicationContext applicationContext;

    public Class<?> getType() {
        return type;
    }

    public MrClientFactoryBean setType(Class<?> type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public MrClientFactoryBean setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public MrClientFactoryBean setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getPath() {
        return path;
    }

    public MrClientFactoryBean setPath(String path) {
        this.path = path;
        return this;
    }

    public Class<? extends MrcInterceptor> getInterceptorClazz() {
        return interceptorClazz;
    }

    public MrClientFactoryBean setInterceptorClazz(Class<? extends MrcInterceptor> interceptorClazz) {
        this.interceptorClazz = interceptorClazz;
        return this;
    }

    public MrClientProperties.CircuitBreakerProperties getCircuitBreakerProperties() {
        return circuitBreakerProperties;
    }

    public MrClientFactoryBean setCircuitBreakerProperties(MrClientProperties.CircuitBreakerProperties circuitBreakerProperties) {
        this.circuitBreakerProperties = circuitBreakerProperties;
        return this;
    }

    public MrClientProperties.RetryProperties getRetryProperties() {
        return retryProperties;
    }

    public MrClientFactoryBean setRetryProperties(MrClientProperties.RetryProperties retryProperties) {
        this.retryProperties = retryProperties;
        return this;
    }

    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    public MrClientFactoryBean setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
        return this;
    }

    /**
     * 构建一个 client bean
     * 根据 name 找到注册中心里的可用服务的地址 此地址优先级 > 手动配置的 url
     * TODO: 完善加入 多个地址 负载均衡选择
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object getObject() throws Exception {
        MrClientContext mrClientContext = applicationContext.getBean(MrClientContext.class);
        MrClientsNamingService clientsNamingService = applicationContext.getBean(MrClientsNamingService.class);

        String instanceUrl = clientsNamingService.getHealthyUrl(this.name);
        if (StringUtils.hasText(instanceUrl)) {
            this.url = instanceUrl;
        }
        if (!StringUtils.hasText(url)) {
            // 没有从注册中心发现服务地址 也没有找到相关配置 异常仅输出日志,不抛出
            logger.error("[mr-client] create client of {} error: {}", this.name, "url cannot be  empty");
            Retrofit retrofit = mrClientContext.addService(name, HTTP_PREFIX + InetAddress.getLocalHost().getHostAddress() + SLASH + this.name + SLASH,
                    instantiateInterceptor(), makeCircuitBreaker(), makeRetry());
            return retrofit.create(type);
        }
        String[] urlParts = this.url.split(",");
        if (urlParts.length > 1) {
           // TODO: 使用负载获取到一个 url
            if (loadBalancer != null) {
                this.url = loadBalancer.getNextResource();
            } else {
                // 默认使用第一个
                this.url = urlParts[0];
            }
        }
        if (StringUtils.hasText(this.url) && !this.url.startsWith(HTTP)) {
            this.url = HTTP_PREFIX + this.url;
        }
        String nUrl = (this.url.endsWith(SLASH) ? this.url : this.url.concat(SLASH)) + cleanPath();
        if (!nUrl.endsWith(SLASH)) {
            nUrl = nUrl.concat(SLASH);
        }
        // 验证 url是否可用
        try {
            new URL(nUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(nUrl + " is malformed", e);
        }
        Retrofit retrofit = mrClientContext.addService(name, nUrl, instantiateInterceptor(), makeCircuitBreaker(), makeRetry());
        return retrofit.create(type);
    }

    private Retry makeRetry() {
        return retryProperties.isEnabled() ? Retry.of(name.concat("_Retry"), RetryConfig.custom()
                .maxAttempts(retryProperties.getMaxAttempts())
                .waitDuration(retryProperties.getWaitDuration())
                .retryExceptions(IOException.class, TimeoutException.class)
                .build()) : null;
    }

    private CircuitBreaker makeCircuitBreaker() {
        return circuitBreakerProperties.isEnabled() ? CircuitBreaker.of(name.concat("_CircuitBreaker"), CircuitBreakerConfig.custom()
                .failureRateThreshold(circuitBreakerProperties.getFailureRateThreshold())
                .slowCallDurationThreshold(Duration.ofSeconds(30))
                .waitDurationInOpenState(circuitBreakerProperties.getWaitDurationInOpenState())
                .permittedNumberOfCallsInHalfOpenState(circuitBreakerProperties.getPermittedNumberOfCallsInHalfOpenState())
                .slidingWindowSize(circuitBreakerProperties.getSlidingWindowSize())
                .minimumNumberOfCalls(6)
                .recordExceptions(IOException.class, TimeoutException.class)
                .build()) : null;
    }

    /**
     * 实例化 interceptor
     *
     * @return
     */
    private MrcInterceptor instantiateInterceptor() {
        // 优先从 spring context 里获取实例
        MrcInterceptor interceptor = null;
        try {
            interceptor = applicationContext.getBean(this.interceptorClazz);
        } catch (BeansException e) {
            logger.warn(e.getLocalizedMessage());
            // 直接实例化
            try {
                interceptor = this.interceptorClazz.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                logger.error(e.getLocalizedMessage());
            }
        }
        return interceptor;
    }


    /**
     * /a/b  --> a/b
     * a/b/  ---> a/b
     *
     * @return
     */
    private String cleanPath() {
        String myPath = this.path.trim();
        if (StringUtils.hasLength(myPath)) {
            if (myPath.startsWith(SLASH)) {
                myPath = myPath.substring(1);
            }
            if (myPath.endsWith(SLASH)) {
                myPath = myPath.substring(0, myPath.length() - 1);
            }
        }
        return myPath;
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.name, "Name must be set");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
