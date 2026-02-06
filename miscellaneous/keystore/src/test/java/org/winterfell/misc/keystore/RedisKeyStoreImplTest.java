package org.winterfell.misc.keystore;

import org.junit.jupiter.api.BeforeAll;
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
public class RedisKeyStoreImplTest extends AbstractKeyStoreTest {

    @BeforeAll
    static void beforeAll() {
        KeyStoreProperties properties = new KeyStoreProperties();
        properties.setType(RedisKeyStoreImpl.NAME);
        keyStore = SimpleKeyStoreFactory.getInstance().getKeyStore(properties);
    }

    @Test
    void set() {
        assertTrue(keyStore.set("test", "Alex"));
    }

    @Test
    void setnx() {
        boolean b = keyStore.setnx("hello", "world");
        assertTrue(b);
    }

    @Test
    void setex() throws InterruptedException {
        boolean b = keyStore.setex("greeting", "hello world", 10);
        if (b) {
            Thread.sleep(12000L);
            System.out.println(keyStore.has("greeting"));
        }
    }

    @Test
    void get() {
        String test = keyStore.get("test");
        assertEquals("Alex", test);
    }

    @Test
    void has() {
        assertTrue(keyStore.has("test"));
    }

    @Test
    void ttl() {
    }

    @Test
    void remove() {
    }

    @Test
    void keys() {
    }
}