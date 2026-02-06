package org.winterfell.misc.remote.mrc;

import org.winterfell.misc.remote.mrc.autoconfigure.MrClientProperties;
import org.winterfell.misc.remote.mrc.interceptor.MrcInterceptor;
import org.winterfell.misc.remote.mrc.loadbalance.LoadBalancerFactory;
import org.winterfell.misc.remote.mrc.loadbalance.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static org.winterfell.misc.remote.mrc.MrClientFactoryBean.*;
import static org.winterfell.misc.remote.mrc.support.MrConstants.*;

/**
 * <p>
 * 注册 {@link MrClient}
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/4/10
 */
public class MrClientsRegistrar implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(MrClientsRegistrar.class);

    private static final LoadBalancerFactory loadBalancerFactory = new LoadBalancerFactory();

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    private Environment environment;

    public MrClientsRegistrar() {
        // do nothing
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerMrClients(metadata, registry);
    }

    /**
     * 注册 {@link MrClient} 注解的 interface
     *
     * @param metadata
     * @param registry
     */
    public void registerMrClients(AnnotationMetadata metadata,
                                  BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableMrClients.class.getName());
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(MrClient.class);
        // 获取 @EnableMrClients 中 clients 注解属性
        final Class<?>[] clients = attrs == null ? null : (Class<?>[]) attrs.get("clients");
        Set<String> basePackages;
        if (clients == null || clients.length == 0) {
            scanner.addIncludeFilter(annotationTypeFilter);
            basePackages = getBasePackages(metadata, attrs);
        } else {
            final Set<String> clientClasses = new HashSet<>();
            basePackages = new HashSet<>();
            for (Class<?> clazz : clients) {
                basePackages.add(ClassUtils.getPackageName(clazz));
                clientClasses.add(clazz.getCanonicalName());
            }
            AbstractClassTestingTypeFilter filter = new AbstractClassTestingTypeFilter() {
                @Override
                protected boolean match(ClassMetadata metadata) {
                    String cleaned = metadata.getClassName().replaceAll("\\$", ".");
                    return clientClasses.contains(cleaned);
                }
            };
            scanner.addIncludeFilter(
                    new AllTypeFilter(Arrays.asList(filter, annotationTypeFilter)));
        }

        // 处理注解的包
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner
                    .findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(),
                            "@MrClient can only be specified on an interface");

                    Map<String, Object> attributes = annotationMetadata
                            .getAnnotationAttributes(
                                    MrClient.class.getCanonicalName());
                    // 注册客户端
                    registerMrClient(registry, annotationMetadata, attributes);
                }
            }
        }
    }

    /**
     * register {@link MrClient}
     *
     * @param registry
     * @param annotationMetadata
     * @param attributes
     */
    @SuppressWarnings("unchecked")
    private void registerMrClient(BeanDefinitionRegistry registry,
                                  AnnotationMetadata annotationMetadata,
                                  Map<String, Object> attributes) {


        String className = annotationMetadata.getClassName();
        // 属性参考: MrClientFactoryBean
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(MrClientFactoryBean.class);
        String name = getClientName(attributes);
        String url = getUrl(attributes);
        // 收集 client 注解中指定的 拦截器
        Class<? extends MrcInterceptor> interceptorClazz = (Class<? extends MrcInterceptor>) attributes.get("interceptor");
        AnnotationAttributes retryAttributes = (AnnotationAttributes) attributes.get("retry");
        AnnotationAttributes circuitBreakerAttributes = (AnnotationAttributes) attributes.get("circuitBreaker");
        String loadBalancerName = String.valueOf(attributes.get("loadBalancer"));

        definition.addPropertyValue(PROPERTY_INTERCEPTOR, interceptorClazz);
        // 设置 bean 其他属性
        definition.addPropertyValue(PROPERTY_URL, url);
        definition.addPropertyValue(PROPERTY_PATH, getPath(attributes));
        definition.addPropertyValue(PROPERTY_NAME, name);
        definition.addPropertyValue(PROPERTY_TYPE, className);
        definition.addPropertyValue(PROPERTY_CIRCUIT_BREAKER, breakerParamToProperties(circuitBreakerAttributes));
        definition.addPropertyValue(PROPERTY_RETRY, retryParamToProperties(retryAttributes));
        definition.addPropertyValue(PROPERTY_LOAD_BALANCER, loadBalancerFactory.getLoadBalancer(loadBalancerName,
                Arrays.stream(url.split(",")).map(u->new Resource().setUrl(u)).collect(Collectors.toList())));

///        definition.addPropertyValue("decode404", attributes.get("decode404"));
///        definition.addPropertyValue("fallback", attributes.get("fallback"));
///        definition.addPropertyValue("fallbackFactory", attributes.get("fallbackFactory"));

        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        String alias = className + BASE_NAME;
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setPrimary(true);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                new String[]{alias});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private MrClientProperties.RetryProperties retryParamToProperties(AnnotationAttributes param) {
        return MrClientProperties.RetryProperties.of().setEnabled(param.getBoolean("enabled"))
                .setWaitDuration(Duration.ofSeconds(param.getNumber("waitDurationInSeconds").longValue()))
                .setMaxAttempts(param.getNumber("maxAttempts").intValue());
    }

    private MrClientProperties.CircuitBreakerProperties breakerParamToProperties(AnnotationAttributes param) {
        return MrClientProperties.CircuitBreakerProperties.of()
                .setEnabled(param.getBoolean("enabled"))
                .setSlidingWindowSize(2)
                .setWaitDurationInOpenState(Duration.ofSeconds(param.getNumber("waitDurationInOpenStateSeconds").longValue()))
                .setPermittedNumberOfCallsInHalfOpenState(param.getNumber("permittedNumberOfCallsInHalfOpenState").intValue())
                .setFailureRateThreshold(param.getNumber("failureRateThreshold").floatValue());
    }

    /**
     * get name of {@link MrClient}
     *
     * @param clientAttrs
     * @return
     */
    private String getClientName(Map<String, Object> clientAttrs) {
        if (clientAttrs == null) {
            return null;
        }
        String value = (String) clientAttrs.get("value");
        if (!StringUtils.hasText(value)) {
            value = (String) clientAttrs.get("name");
        }
        if (StringUtils.hasText(value)) {
            return value.startsWith("${") ? resolve(value) : value;
        }
        throw new IllegalStateException("Either 'name' or 'value' must be provided in @"
                + MrClient.class.getSimpleName());
    }

    /**
     * 获取注解的 base packages
     *
     * @param importingClassMetadata
     * @param attributes
     * @return
     */
    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata, Map<String, Object> attributes) {
        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    /**
     * 类扫描的 provider
     *
     * @return
     */
    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    // TODO until SPR-11711 will be resolved
                    if (beanDefinition.getMetadata().isInterface()
                            && beanDefinition.getMetadata()
                            .getInterfaceNames().length == 1
                            && Annotation.class.getName().equals(beanDefinition
                            .getMetadata().getInterfaceNames()[0])) {
                        try {
                            Class<?> target = ClassUtils.forName(
                                    beanDefinition.getMetadata().getClassName(),
                                    MrClientsRegistrar.this.classLoader);
                            return !target.isAnnotation();
                        } catch (Exception ex) {
                            this.logger.error(
                                    "Could not load target class: "
                                            + beanDefinition.getMetadata().getClassName(),
                                    ex);

                        }
                    }
                    return true;
                }
                return false;

            }
        };
    }

    /**
     * get {@link MrClient#url()}
     *
     * @param attributes
     * @return
     */
    private String getUrl(Map<String, Object> attributes) {
        if (Objects.isNull(attributes)) {
            return "";
        }
        String url = resolve((String) attributes.get(PROPERTY_URL));
        if (StringUtils.hasText(url) && !(url.startsWith(PLACEHOLDER_PREFIX) && url.contains(PLACEHOLDER_SUFFIX))) {
            if (!url.contains("://")) {
                url = HTTP_PREFIX + url;
            }
        } else {
            return "";
        }
        return url;
    }

    /**
     * get {@link MrClient#path()}
     *
     * @param attributes
     * @return
     */
    private String getPath(Map<String, Object> attributes) {
        if (Objects.isNull(attributes)) {
            return "";
        }
        String path = resolve((String) attributes.get(PROPERTY_PATH));
        if (StringUtils.hasText(path)) {
            path = path.trim();
            if (!path.startsWith(SLASH)) {
                path = SLASH + path;
            }
            if (path.endsWith(SLASH)) {
                path = path.substring(0, path.length() - 1);
            }
        }
        return path;
    }

    /**
     * resolve spring env
     *
     * @param value
     * @return
     */
    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }

    /**
     * Helper class to create a {@link TypeFilter} that matches if all the delegates
     * match.
     *
     * @author Oliver Gierke
     */
    private static class AllTypeFilter implements TypeFilter {

        private final List<TypeFilter> delegates;

        /**
         * Creates a new {@link AllTypeFilter} to match if all the given delegates match.
         *
         * @param delegates must not be {@literal null}.
         */
        public AllTypeFilter(List<TypeFilter> delegates) {

            Assert.notNull(delegates, "`delegates` cannot be null");
            this.delegates = delegates;
        }

        @Override
        public boolean match(MetadataReader metadataReader,
                             MetadataReaderFactory metadataReaderFactory) throws IOException {

            for (TypeFilter filter : this.delegates) {
                if (!filter.match(metadataReader, metadataReaderFactory)) {
                    return false;
                }
            }

            return true;
        }
    }
}
