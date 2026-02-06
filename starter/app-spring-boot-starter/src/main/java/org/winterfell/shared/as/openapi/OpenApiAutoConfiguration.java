package org.winterfell.shared.as.openapi;

import org.winterfell.shared.as.openapi.knife4j.Knife4jOpenApiCustomizer;
import org.winterfell.shared.as.openapi.knife4j.Knife4jOperationCustomizer;
import org.winterfell.shared.as.openapi.knife4j.filter.ProductionSecurityFilter;
import org.winterfell.shared.as.openapi.knife4j.util.EnvironmentUtils;
import com.github.xiaoymin.knife4j.core.conf.GlobalConstants;
import com.github.xiaoymin.knife4j.extend.filter.basic.ServletSecurityBasicAuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.DispatcherType;

/**
 * <p>
 * 自动注册 openapi 文档相关的bean 等
 * </p>
 *
 * @author Alex
 * @since 2025/9/25
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({OpenApiProperties.class})
public class OpenApiAutoConfiguration {

    private final Environment environment;

    public OpenApiAutoConfiguration(Environment environment) {
        this.environment = environment;
    }

    /**
     * 增强自定义配置
     *
     * @param properties
     * @param docProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public Knife4jOpenApiCustomizer knife4jOpenApiCustomizer(OpenApiProperties properties, SpringDocConfigProperties docProperties) {
        log.info("[openapi] Register Knife4jOpenApiCustomizer");
        return new Knife4jOpenApiCustomizer(properties, docProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public Knife4jOperationCustomizer knife4jOperationCustomizer(OpenApiProperties properties) {
        log.info("[openapi] Register Knife4jOperationCustomizer");
        return new Knife4jOperationCustomizer(properties);
    }

    /**
     * 配置Cors
     *
     * @return
     */
    @Bean("knife4jCorsFilter")
    @ConditionalOnMissingBean(CorsFilter.class)
    @ConditionalOnProperty(name = "application.openapi.cors", havingValue = "true")
    public CorsFilter corsFilter() {
        log.info("[openapi] init CorsFilter...");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setMaxAge(10000L);
        // 匹配所有API
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    /**
     * Security with Basic Http
     *
     * @param knife4jProperties Basic Properties
     * @return BasicAuthFilter
     */
    @Bean
    @ConditionalOnMissingBean(ServletSecurityBasicAuthFilter.class)
    @ConditionalOnProperty(name = "application.openapi.basic.enabled", havingValue = "true")
    public FilterRegistrationBean<ServletSecurityBasicAuthFilter> securityBasicAuthFilter(OpenApiProperties knife4jProperties) {
        log.info("[openapi] init ServletSecurityBasicAuthFilter...");
        ServletSecurityBasicAuthFilter authFilter = new ServletSecurityBasicAuthFilter();
        if (knife4jProperties == null) {
            authFilter.setEnableBasicAuth(EnvironmentUtils.resolveBool(environment, "openapi.basic.enabled", Boolean.FALSE));
            authFilter.setUserName(EnvironmentUtils.resolveString(environment, "openapi.basic.username", GlobalConstants.BASIC_DEFAULT_USERNAME));
            authFilter.setPassword(EnvironmentUtils.resolveString(environment, "openapi.basic.password", GlobalConstants.BASIC_DEFAULT_PASSWORD));
        } else {
            // 判断非空
            if (knife4jProperties.getBasic() == null) {
                authFilter.setEnableBasicAuth(Boolean.FALSE);
                authFilter.setUserName(GlobalConstants.BASIC_DEFAULT_USERNAME);
                authFilter.setPassword(GlobalConstants.BASIC_DEFAULT_PASSWORD);
            } else {
                authFilter.setEnableBasicAuth(knife4jProperties.getBasic().isEnabled());
                authFilter.setUserName(knife4jProperties.getBasic().getUsername());
                authFilter.setPassword(knife4jProperties.getBasic().getPassword());
                authFilter.addRule(knife4jProperties.getBasic().getInclude());
            }
        }
        FilterRegistrationBean<ServletSecurityBasicAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(authFilter);
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(ProductionSecurityFilter.class)
    @ConditionalOnProperty(name = "application.openapi.production", havingValue = "true")
    public FilterRegistrationBean<ProductionSecurityFilter> productionSecurityFilter(OpenApiProperties knife4jProperties) {
        boolean prod = false;
        ProductionSecurityFilter p = null;
        if (knife4jProperties == null) {
            if (environment != null) {
                String prodStr = environment.getProperty("application.openapi.production");
                if (log.isDebugEnabled()) {
                    log.debug("swagger.production:{}", prodStr);
                }
                prod = Boolean.parseBoolean(prodStr);
            }
            p = new ProductionSecurityFilter(prod);
        } else {
            p = new ProductionSecurityFilter(knife4jProperties.isProduction());
        }
        FilterRegistrationBean<ProductionSecurityFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(p);
        registration.setOrder(Integer.MAX_VALUE - 1);
        return registration;
    }
}