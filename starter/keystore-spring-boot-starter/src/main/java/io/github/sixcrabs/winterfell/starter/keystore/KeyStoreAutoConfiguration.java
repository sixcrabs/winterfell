package io.github.sixcrabs.winterfell.starter.keystore;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import io.github.sixcrabs.winterfell.keystore.KeyStoreProperties;
import io.github.sixcrabs.winterfell.keystore.SimpleKeyStore;
import io.github.sixcrabs.winterfell.keystore.SimpleKeyStoreFactory;

/**
 * <p>
 * auto configuration
 * </p>
 *
 * @author Alex
 * @since 2025/10/23
 */
@AutoConfiguration
public class KeyStoreAutoConfiguration {

    @Bean
    @Order(-1)
    @ConfigurationProperties(prefix = "keystore")
    public KeyStoreProperties keyStoreProperties() {
        return new KeyStoreProperties();
    }

    @Bean(destroyMethod = "destroy")
    @Order(0)
    public SimpleKeyStore keyStore(KeyStoreProperties properties) {
        return SimpleKeyStoreFactory.getInstance().getKeyStore(properties);
    }
}