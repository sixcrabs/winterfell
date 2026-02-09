package org.winterfell.misc.keystore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/21
 */
public class SimpleKeyStoreFactoryTest {

    @Test
    void getKeyStore() {
        KeyStoreProperties properties = new KeyStoreProperties();
        properties.setType("memory");
        SimpleKeyStore keyStore = SimpleKeyStoreFactory.getInstance().getKeyStore(properties);
        boolean contains = keyStore.has("name");
        if (!contains) {
            keyStore.setex("name", "Alex", 60);
        }
        String val = keyStore.get("name");
        System.out.println(val);
    }

    @Test
    void testFastUtil() {
        KeyStoreProperties properties = new KeyStoreProperties();
        properties.setType("fastutil");
        SimpleKeyStore keyStore = SimpleKeyStoreFactory.getInstance().getKeyStore(properties);
        boolean b = keyStore.set("name", "Alex");
        System.out.println(b);
        String name = keyStore.get("name");
        System.out.println(name);
    }
}