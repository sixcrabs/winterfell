package org.winterfell.misc.keystore;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * .`fastutil` 实现
 * </p>
 *
 * @author Alex
 * @since 2025/12/17
 */
public class FastutilKeyStoreImpl extends AbstractKeyStoreImpl {

    public static final String NAME = "fastutil";

    // 字典
    private final Object2IntOpenHashMap<String> dict;
    // 业务数据
    private final Int2ObjectOpenHashMap<Object> data;
    private final AtomicInteger idGen = new AtomicInteger(1);


    public FastutilKeyStoreImpl() {
        this.dict = new Object2IntOpenHashMap<>(1 << 20);
        this.data = new Int2ObjectOpenHashMap<>(1 << 20);
        dict.defaultReturnValue(-1);
    }

    /**
     * initialize
     *
     * @param properties
     */
    @Override
    protected void initialize(KeyStoreProperties properties) {
        // TODO 数据持久化
    }

    /**
     * 实现名称, 区分不同的 SPI 实现，必须
     *
     * @return
     */
    @Override
    public String name() {
        return NAME;
    }

    /**
     * set key
     *
     * @param key
     * @param val
     * @return
     */
    @Override
    public boolean set(String key, Object val) {
        int id = dict.computeIfAbsent(key, k -> idGen.getAndIncrement());
        data.put(id, val);
        return true;
    }

    /**
     * set key if not exists
     *
     * @param key
     * @param val
     * @return
     */
    @Override
    public boolean setnx(String key, Object val) {
        if (!has(key)) {
            return set(key, val);
        }
        return false;
    }

    /**
     * set key with ttl
     *
     * @param key k
     * @param val value
     * @param ttl time to live in `seconds`
     * @return
     */
    @Override
    public boolean setex(String key, Object val, long ttl) {
        boolean b = set(key, val);
        if (b) {
            return addTTLTimer(key, ttl);
        }
        return false;
    }

    /**
     * get key
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        int id = dict.getInt(key);
        return id == -1 ? null : (T) data.get(id);
    }

    /**
     * has key
     *
     * @param key
     * @return
     */
    @Override
    public boolean has(String key) {
        int id = dict.getInt(key);
        return id != -1 && data.containsKey(id);
    }

    /**
     * remove key
     *
     * @param key
     */
    @Override
    public void remove(String key) {
        if (has(key)) {
            int id = dict.getInt(key);
            data.remove(id);
            dict.remove(key, id);
        }
    }

    /**
     * 返回类似的 key 集合
     *
     * @param prefix 前缀 eg: `user_` 即返回所有以此开头的 key
     * @return
     */
    @Override
    public List<String> keys(String prefix) {
        return dict.keySet()
                .stream()
                .filter(k -> k.startsWith(prefix))
                .collect(Collectors.toList());
    }
}