package org.winterfell.misc.hutool.mini.collection;

import org.winterfell.misc.hutool.mini.ClassUtil;
import org.winterfell.misc.hutool.mini.ReflectUtil;
import org.winterfell.misc.hutool.mini.UtilException;

import java.util.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class CollectionUtil {

    /**
     * 创建新的集合对象
     *
     * @param <T> 集合类型
     * @param collectionType 集合类型
     * @return 集合类型对应的实例
     * @since 3.0.8
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Collection<T> create(Class<?> collectionType) {
        Collection<T> list = null;
        if (collectionType.isAssignableFrom(AbstractCollection.class)) {
            // 抽象集合默认使用ArrayList
            list = new ArrayList<>();
        }

        // Set
        else if (collectionType.isAssignableFrom(HashSet.class)) {
            list = new HashSet<>();
        } else if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
            list = new LinkedHashSet<>();
        } else if (collectionType.isAssignableFrom(TreeSet.class)) {
            list = new TreeSet<>();
        } else if (collectionType.isAssignableFrom(EnumSet.class)) {
            list = (Collection<T>) EnumSet.noneOf((Class<Enum>) ClassUtil.getTypeArgument(collectionType));
        }

        // List
        else if (collectionType.isAssignableFrom(ArrayList.class)) {
            list = new ArrayList<>();
        } else if (collectionType.isAssignableFrom(LinkedList.class)) {
            list = new LinkedList<>();
        }

        // Others，直接实例化
        else {
            try {
                list = (Collection<T>) ReflectUtil.newInstance(collectionType);
            } catch (Exception e) {
                throw new UtilException(e);
            }
        }
        return list;
    }

    /**
     * 新建一个HashSet
     *
     * @param <T> 集合元素类型
     * @param ts 元素数组
     * @return HashSet对象
     */
    @SafeVarargs
    public static <T> HashSet<T> newHashSet(T... ts) {
        return newHashSet(false, ts);
    }

    /**
     * 新建一个HashSet
     *
     * @param <T> 集合元素类型
     * @param isSorted 是否有序，有序返回 {@link LinkedHashSet}，否则返回 {@link HashSet}
     * @param ts 元素数组
     * @return HashSet对象
     */
    @SafeVarargs
    public static <T> HashSet<T> newHashSet(boolean isSorted, T... ts) {
        if (null == ts) {
            return isSorted ? new LinkedHashSet<T>() : new HashSet<T>();
        }
        int initialCapacity = Math.max((int) (ts.length / .75f) + 1, 16);
        HashSet<T> set = isSorted ? new LinkedHashSet<T>(initialCapacity) : new HashSet<T>(initialCapacity);
        for (T t : ts) {
            set.add(t);
        }
        return set;
    }
}
