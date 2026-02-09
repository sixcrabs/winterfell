package io.github.sixcrabs.winterfell.starter.rpc.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import io.github.sixcrabs.winterfell.starter.rpc.processor.RpcInjectBeanProcessor;
import io.github.sixcrabs.winterfell.starter.rpc.processor.RpcProviderBeanProcessor;
import io.github.sixcrabs.winterfell.starter.rpc.support.NacosNamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

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