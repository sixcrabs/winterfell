package org.winterfell.starter.keystore;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.winterfell.misc.keystore.KeyStoreProperties;
import org.winterfell.misc.keystore.SimpleKeyStore;
import org.winterfell.misc.keystore.SimpleKeyStoreFactory;

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