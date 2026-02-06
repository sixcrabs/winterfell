package org.winterfell.misc.keystore;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.winterfell.misc.hutool.mini.RandomUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/21
 */
class MvStoreKeyStoreImplTest {

    private static SimpleKeyStore keyStore;

    @BeforeAll
    static void preset() {
        KeyStoreProperties properties = new KeyStoreProperties();
        properties.setType(MvStoreKeyStoreImpl.NAME);
        properties.setUri("/Users/alex/data/swap/keystore_h2.db");
        keyStore = SimpleKeyStoreFactory.getInstance().getKeyStore(properties);
    }

    @Test
    void set() throws InterruptedException {
        boolean b = keyStore.set("name", "Alex");
        if (b) {
            Thread.sleep(10000L);
        }
        assertTrue(b);
    }

    @Test
    void setnx() throws InterruptedException {
        boolean b = keyStore.setnx("my", RandomUtil.randomString(6));
        if (b) {
            Thread.sleep(10000L);
        }
        assertTrue(b);
    }

    @Test
    void setex() throws InterruptedException {
        boolean b = keyStore.setex("hello", "world", 10);
        if (b) {
            Thread.sleep(10000L);
            System.out.println(keyStore.has("hello"));
        }
    }

    @Test
    void get() {
        String name = keyStore.get("name");
        assertEquals("Alex", name);
    }

    @Test
    void has() {
        assertTrue(keyStore.has("my"));
    }

    @Test
    void remove() {
        keyStore.remove("name");
        assertFalse(keyStore.has("name"));
    }
}