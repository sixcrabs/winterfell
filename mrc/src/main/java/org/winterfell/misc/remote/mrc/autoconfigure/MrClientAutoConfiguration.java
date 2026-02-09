package org.winterfell.misc.remote.mrc.autoconfigure;

import org.winterfell.misc.remote.mrc.MrClientsNamingService;
import org.winterfell.misc.remote.mrc.interceptor.HeaderInterceptor;
import org.winterfell.misc.remote.mrc.interceptor.LogInterceptor;
import org.winterfell.misc.remote.mrc.support.MrClientContext;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * mr client 自动注册
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/4/9
 */
@Configuration
@EnableConfigurationProperties(MrClientProperties.class)
public class MrClientAutoConfiguration {

    private final List<MrClientInterceptorConfigurer> interceptorConfigurers;

    private final List<MrClientConfigBuilderCustomizer> builderCustomizers;

    @Autowired
    public MrClientAutoConfiguration(ObjectProvider<List<MrClientInterceptorConfigurer>> interceptorConfigurers,
                                     ObjectProvider<List<MrClientConfigBuilderCustomizer>> builderCustomizers) {
        this.interceptorConfigurers = interceptorConfigurers.getIfAvailable();
        this.builderCustomizers = builderCustomizers.getIfAvailable();
    }

    @Bean
    @Primary
    public MrClientContext mrClientContext(ObjectProvider<MrClientProperties> propertiesObjectProvider) {
        MrClientProperties properties = propertiesObjectProvider.getIfAvailable();
        return new MrClientContext(genericClient(properties), properties);
    }

    @Bean(initMethod = "init")
    @Primary
    public MrClientsNamingService mrClientsNamingService() {
        return new MrClientsNamingService();
    }


    /**
     * generic client
     *
     * @return
     */
    private OkHttpClient genericClient(MrClientProperties properties) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(HeaderInterceptor.INSTANCE);
        if (properties != null && properties.getShowUrl()) {
            builder.addInterceptor(LogInterceptor.INSTANCE);
        }
        customize(builder);
        return builder.build();
    }


    /**
     * customize with interceptor and customizers
     *
     * @param builder
     */
    private void customize(OkHttpClient.Builder builder) {
        if (this.builderCustomizers != null) {
            for (MrClientConfigBuilderCustomizer customizer : this.builderCustomizers) {
                customizer.customize(builder);
            }
        }
        if (this.interceptorConfigurers != null) {
            for (MrClientInterceptorConfigurer configurer : this.interceptorConfigurers) {
                builder.addInterceptor(configurer.config());
            }
        }
    }


}
