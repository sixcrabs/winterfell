package org.winterfell.zinc.starter;

import org.winterfell.misc.zinc.ZinClient;
import org.winterfell.misc.zinc.ZinClientFactory;
import org.winterfell.misc.zinc.http.ZincHttpClientConfig;
import org.winterfell.misc.zinc.http.ZincHttpClientConfigBuilderCustomizer;
import com.google.gson.Gson;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.List;

/**
 * <p>
 * auto config zinc
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
@AutoConfiguration(after = GsonAutoConfiguration.class)
@ConditionalOnClass(ZinClient.class)
@EnableConfigurationProperties({ZincProperties.class})
public class ZincAutoConfiguration {

    private final ObjectProvider<Gson> gsonProvider;

    private final ZincProperties zincProperties;

    private final List<ZincHttpClientConfigBuilderCustomizer> builderCustomizers;

    public ZincAutoConfiguration(ObjectProvider<Gson> gsonProvider, ZincProperties properties,
                                 ObjectProvider<List<ZincHttpClientConfigBuilderCustomizer>> builderCustomizers) {
        this.gsonProvider = gsonProvider;
        this.zincProperties = properties;
        this.builderCustomizers = builderCustomizers.getIfAvailable();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public ZinClient zinClient() {
        ZinClientFactory factory = new ZinClientFactory();
        factory.setHttpClientConfig(createHttpClientConfig());
        return factory.getObject();
    }


    /**
     * create http client config
     *
     * @return
     */
    protected ZincHttpClientConfig createHttpClientConfig() {
        ZincHttpClientConfig.Builder builder = new ZincHttpClientConfig.Builder(zincProperties.getUri());
        PropertyMapper map = PropertyMapper.get();
        ZincProperties.BasicAuthProperties basicAuthProperties = this.zincProperties.getBasicAuth();
        map.from(basicAuthProperties::getUsername).whenHasText().to((username) -> builder
                .defaultCredentials(username, basicAuthProperties.getPassword()));
        map.from(this.gsonProvider::getIfUnique).whenNonNull().to(builder::gson);
        map.from(this.zincProperties::isMultiThreaded).to(builder::multiThreaded);
        map.from(this.zincProperties::isDebugEnabled).to(builder::enableDebugOrNot);
        map.from(this.zincProperties::getConnectionTimeout).whenNonNull()
                .asInt(Duration::toMillis).to(builder::connTimeout);
        map.from(this.zincProperties::getReadTimeout).whenNonNull().asInt(Duration::toMillis)
                .to(builder::readTimeout);
        customize(builder);
        return builder.build();
    }

    private void customize(ZincHttpClientConfig.Builder builder) {
        if (this.builderCustomizers != null) {
            for (ZincHttpClientConfigBuilderCustomizer customizer : this.builderCustomizers) {
                customizer.customize(builder);
            }
        }
    }
}
