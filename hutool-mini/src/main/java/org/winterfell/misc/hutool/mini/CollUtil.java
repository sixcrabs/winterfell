package org.winterfell.misc.hutool.mini;

import org.winterfell.misc.hutool.mini.collection.IterUtil;

import java.util.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/1/4
 */
public class CollUtil {


    /**
     * 新建一个HashMap
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @return HashMap对象
     * @see MapUtil#newHashMap()
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return MapUtil.newHashMap();
    }

    /**
     * 新建一个HashMap
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param size 初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75
     * @param isOrder Map的Key是否有序，有序返回 {@link LinkedHashMap}，否则返回 {@link HashMap}
     * @return HashMap对象
     * @since 3.0.4
     * @see MapUtil#newHashMap(int, boolean)
     */
    public static <K, V> HashMap<K, V> newHashMap(int size, boolean isOrder) {
        return MapUtil.newHashMap(size, isOrder);
    }

    /**
     * 新建一个HashMap
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param size 初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75
     * @return HashMap对象
     * @see MapUtil#newHashMap(int)
     */
    public static <K, V> HashMap<K, V> newHashMap(int size) {
        return MapUtil.newHashMap(size);
    }

    /**
     * 新建一个HashSet
     *
     * @param <T> 集合元素类型
     * @param ts  元素数组
     * @return HashSet对象
     */
    @SafeVarargs
    public static <T> HashSet<T> newHashSet(T... ts) {
        return set(false, ts);
    }

    /**
     * 新建一个HashSet
     *
     * @param <T>      集合元素类型
     * @param isSorted 是否有序，有序返回 {@link LinkedHashSet}，否则返回 {@link HashSet}
     * @param ts       元素数组
     * @return HashSet对象
     */
    @SafeVarargs
    public static <T> HashSet<T> set(boolean isSorted, T... ts) {
        if (null == ts) {
            return isSorted ? new LinkedHashSet<>() : new HashSet<>();
        }
        int initialCapacity = Math.max((int) (ts.length / .75f) + 1, 16);
        final HashSet<T> set = isSorted ? new LinkedHashSet<>(initialCapacity) : new HashSet<>(initialCapacity);
        Collections.addAll(set, ts);
        return set;
    }

    /**
     * 集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Map是否为非空
     *
     * @param map 集合
     * @return 是否为非空
     * @see MapUtil#isNotEmpty(Map)
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return MapUtil.isNotEmpty(map);
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     * @see IterUtil#isNotEmpty(Iterable)
     */
    public static boolean isNotEmpty(Iterable<?> iterable) {
        return IterUtil.isNotEmpty(iterable);
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     * @see IterUtil#isNotEmpty(Iterator)
     */
    public static boolean isNotEmpty(Iterator<?> Iterator) {
        return IterUtil.isNotEmpty(Iterator);
    }

    /**
     * Enumeration是否为空
     *
     * @param enumeration {@link Enumeration}
     * @return 是否为空
     */
    public static boolean isNotEmpty(Enumeration<?> enumeration) {
        return null != enumeration && enumeration.hasMoreElements();
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iterable 被检查的Iterable对象，如果为{@code null} 返回false
     * @return 是否包含{@code null}元素
     * @since 3.0.7
     * @see IterUtil#hasNull(Iterable)
     */
    public static boolean hasNull(Iterable<?> iterable) {
        return IterUtil.hasNull(iterable);
    }

    /**
     * 映射键值（参考Python的zip()函数）<br>
     * 例如：<br>
     * keys = a,b,c,d<br>
     * values = 1,2,3,4<br>
     * delimiter = , 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param keys 键列表
     * @param values 值列表
     * @param delimiter 分隔符
     * @param isOrder 是否有序
     * @return Map
     * @since 3.0.4
     */
    public static Map<String, String> zip(String keys, String values, String delimiter, boolean isOrder) {
        return ArrayUtil.zip(StringUtil.split(keys, delimiter), StringUtil.split(values, delimiter), isOrder);
    }

    /**
     * 映射键值（参考Python的zip()函数），返回Map无序<br>
     * 例如：<br>
     * keys = a,b,c,d<br>
     * values = 1,2,3,4<br>
     * delimiter = , 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param keys 键列表
     * @param values 值列表
     * @param delimiter 分隔符
     * @return Map
     */
    public static Map<String, String> zip(String keys, String values, String delimiter) {
        return zip(keys, values, delimiter, false);
    }

    /**
     * 映射键值（参考Python的zip()函数）<br>
     * 例如：<br>
     * keys = [a,b,c,d]<br>
     * values = [1,2,3,4]<br>
     * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param <K> 键类型
     * @param <V> 值类型
     * @param keys 键列表
     * @param values 值列表
     * @return Map
     */
    public static <K, V> Map<K, V> zip(Collection<K> keys, Collection<V> values) {
        if (isEmpty(keys) || isEmpty(values)) {
            return null;
        }

        final List<K> keyList = new ArrayList<K>(keys);
        final List<V> valueList = new ArrayList<V>(values);

        final int size = Math.min(keys.size(), values.size());
        final Map<K, V> map = new HashMap<K, V>((int) (size / 0.75));
        for (int i = 0; i < size; i++) {
            map.put(keyList.get(i), valueList.get(i));
        }

        return map;
    }
}
