package org.winterfell.misc.keystore;

import java.util.List;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/20
 */
public interface SimpleKeyStore {

    /**
     * 实现名称, 区分不同的 SPI 实现，必须
     * @return
     */
    String name();

    /**
     * do destroy
     */
    default void destroy() {}

    /**
     * set key
     *
     * @param key
     * @param val
     * @return
     */
    boolean set(String key, Object val);

    /**
     * set key if not exists
     *
     * @param key
     * @param val
     * @return
     */
    boolean setnx(String key, Object val);

    /**
     * set key with ttl
     *
     * @param key k
     * @param val value
     * @param ttl time to live in `seconds`
     * @return
     */
    boolean setex(String key, Object val, long ttl);

    /**
     * get key
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> T get(String key);

    /**
     * has key
     *
     * @param key
     * @return
     */
    boolean has(String key);

    /**
     * remove key
     *
     * @param key
     */
    void remove(String key);

    /**
     * 返回类似的 key 集合
     * @param prefix 前缀 eg: `user_` 即返回所有以此开头的 key
     * @return
     */
    List<String> keys(String prefix);

    /**
     * add listener
     *
     * @param key      data key
     * @param listener listener
     * @throws KeyStoreException
     */
    void addListener(String key, KeyEventListener listener) throws KeyStoreException;

    /**
     * 当前key 的ttl时间 单位秒
     * 默认都返回 -1L， 只有 redis 才支持
     * @param key
     * @return
     */
    default long ttl(String key) {
        return -1L;
    }

    String EXPIRED = "expired";
    String REMOVED = "removed";

    interface KeyEventListener {

        /**
         * on event
         *
         * @param eventType event type
         * @param key       data key
         * @param val       data value
         */
        void onEvent(String eventType, String key, Object val);
    }
}
