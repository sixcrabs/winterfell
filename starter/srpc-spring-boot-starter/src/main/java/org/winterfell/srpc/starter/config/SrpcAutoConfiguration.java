package org.winterfell.srpc.starter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.winterfell.srpc.starter.processor.RpcInjectBeanProcessor;
import org.winterfell.srpc.starter.processor.RpcProviderBeanProcessor;
import org.winterfell.srpc.starter.support.NacosNamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/19
 */
@AutoConfiguration
@EnableConfigurationProperties(SrpcProperties.class)
public class SrpcAutoConfiguration {

    private final SrpcProperties properties;

    @Autowired
    public SrpcAutoConfiguration(SrpcProperties properties) {
        this.properties = properties;
    }

    @Bean(initMethod = "init")
    public NacosNamingService nacosNamingService() {
        return new NacosNamingService();
    }

    @Bean
    public RpcProviderBeanProcessor rpcServiceBeanProcessor() {
        if (!Objects.isNull(properties.getServer())) {
            return new RpcProviderBeanProcessor(properties.getServer());
        }
        return null;
    }

    @Bean
    public RpcInjectBeanProcessor rpcInjectBeanProcessor(NacosNamingService namingService) {
        return new RpcInjectBeanProcessor(properties, namingService);
    }
}
