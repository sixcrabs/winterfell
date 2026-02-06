package org.winterfell.misc.zinc.support;

import org.winterfell.misc.zinc.exception.ZincException;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/4/20
 */
public final class ZincUtil {

    private ZincUtil() {
    }

    public static String toUnderlineCase(CharSequence str) {
        return toSymbolCase(str, '_');
    }

    public static String toKebabCase(CharSequence str) {
        return toSymbolCase(str, '-');
    }

    public static String toSymbolCase(CharSequence str, char symbol) {
        if (str == null) {
            return null;
        } else {
            int length = str.length();
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < length; ++i) {
                char c = str.charAt(i);
                if (Character.isUpperCase(c)) {
                    Character preChar = i > 0 ? str.charAt(i - 1) : null;
                    Character nextChar = i < str.length() - 1 ? str.charAt(i + 1) : null;
                    if (null != preChar) {
                        if (symbol == preChar) {
                            if (null == nextChar || Character.isLowerCase(nextChar)) {
                                c = Character.toLowerCase(c);
                            }
                        } else if (Character.isLowerCase(preChar)) {
                            sb.append(symbol);
                            if (null == nextChar || Character.isLowerCase(nextChar) || isNumber(nextChar)) {
                                c = Character.toLowerCase(c);
                            }
                        } else if (null == nextChar || Character.isLowerCase(nextChar)) {
                            sb.append(symbol);
                            c = Character.toLowerCase(c);
                        }
                    } else if (null == nextChar || Character.isLowerCase(nextChar)) {
                        c = Character.toLowerCase(c);
                    }
                }

                sb.append(c);
            }

            return sb.toString();
        }
    }

    public static boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }


    public static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static <T> boolean isEmpty(final T... array) {
        return array == null || array.length == 0;
    }

    /** 默认初始大小 */
    public static final int MAP_DEFAULT_INITIAL_CAPACITY = 16;
    /** 默认增长因子，当Map的size达到 容量*增长因子时，开始扩充Map */
    public static final float MAP_DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 新建一个HashMap
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param size 初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75
     * @param isOrder Map的Key是否有序，有序返回 {@link LinkedHashMap}，否则返回 {@link HashMap}
     * @return HashMap对象
     * @since 3.0.4
     */
    public static <K, V> HashMap<K, V> newHashMap(int size, boolean isOrder) {
        int initialCapacity = (int) (size / MAP_DEFAULT_LOAD_FACTOR);
        return isOrder ? new LinkedHashMap<K, V>(initialCapacity) : new HashMap<K, V>(initialCapacity);
    }

    /**
     * 新建一个HashMap
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param isOrder Map的Key是否有序，有序返回 {@link LinkedHashMap}，否则返回 {@link HashMap}
     * @return HashMap对象
     */
    public static <K, V> HashMap<K, V> newHashMap(boolean isOrder) {
        return newHashMap(MAP_DEFAULT_INITIAL_CAPACITY, isOrder);
    }

    /**
     * 新建一个HashMap
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @return HashMap对象
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>(1);
    }

    /**
     * 新建一个ArrayList
     *
     * @param <T> 集合元素类型
     * @param values 数组
     * @return ArrayList对象
     */
    @SafeVarargs
    public static <T> ArrayList<T> newArrayList(T... values) {
        if (null == values) {
            return new ArrayList<>();
        }
        ArrayList<T> arrayList = new ArrayList<T>(values.length);
        for (T t : values) {
            arrayList.add(t);
        }
        return arrayList;
    }

    /**
     * 实例化对象
     *
     * @param <T> 对象类型
     * @param clazz 类
     * @param params 构造函数参数
     * @return 对象
     * @throws ZincException 包装各类异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz, Object... params) throws ZincException {
        if (isEmpty(params)) {
            if(Map.class.isAssignableFrom(clazz)) {
                //Map
                if(LinkedHashMap.class.isAssignableFrom(clazz)) {
                    return (T) newHashMap(true);
                }else {
                    return (T) newHashMap();
                }
            } else if(Iterable.class.isAssignableFrom(clazz)) {
                //Iterable
                if(LinkedHashSet.class.isAssignableFrom(clazz)) {
                    return (T) new LinkedHashSet<>();
                }else if(Set.class.isAssignableFrom(clazz)) {
                    return (T) new HashSet<>();
                } else if(LinkedList.class.isAssignableFrom(clazz)) {
                    return (T) new LinkedList<>();
                } else {
                    return (T) newArrayList();
                }
            }

            try {
                return (T) clazz.newInstance();
            } catch (Exception e) {
                throw new ZincException(String.format("Instance class [%s] error!", clazz.getSimpleName()), e);
            }
        }

        final Class<?>[] paramTypes = getClasses(params);
        final Constructor<?> constructor = getConstructor(clazz, paramTypes);
        if (null == constructor) {
            throw new ZincException(String.format("No Constructor matched for parameter types: [%s]",
                    Arrays.stream(paramTypes).map(Class::getSimpleName).collect(Collectors.joining(","))));
        }
        try {
            return getConstructor(clazz, paramTypes).newInstance(params);
        } catch (Exception e) {
            throw new ZincException(String.format("Instance class [%s] error!", clazz.getSimpleName()), e);
        }
    }


    /**
     * 获得对象数组的类数组
     *
     * @param objects 对象数组，如果数组中存在{@code null}元素，则此元素被认为是Object类型
     * @return 类数组
     */
    public static Class<?>[] getClasses(Object... objects) {
        Class<?>[] classes = new Class<?>[objects.length];
        Object obj;
        for (int i = 0; i < objects.length; i++) {
            obj = objects[i];
            classes[i] = (null == obj) ? Object.class : obj.getClass();
        }
        return classes;
    }

    /**
     * 查找类中的指定参数的构造方法
     *
     * @param <T> 对象类型
     * @param clazz 类
     * @param parameterTypes 参数类型，只要任何一个参数是指定参数的父类或接口或相等即可
     * @return 构造方法，如果未找到返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        if (null == clazz) {
            return null;
        }

        final Constructor<?>[] constructors = clazz.getConstructors();
        Class<?>[] pts;
        for (Constructor<?> constructor : constructors) {
            pts = constructor.getParameterTypes();
            if (isAllAssignableFrom(pts, parameterTypes)) {
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }

    /**
     * 比较判断types1和types2两组类，如果types1中所有的类都与types2对应位置的类相同，或者是其父类或接口，则返回<code>true</code>
     *
     * @param types1 类组1
     * @param types2 类组2
     * @return 是否相同、父类或接口
     */
    public static boolean isAllAssignableFrom(Class<?>[] types1, Class<?>[] types2) {
        if (isEmpty(types1) && isEmpty(types2)) {
            return true;
        }
        if (types1.length != types2.length) {
            return false;
        }

        Class<?> type1;
        Class<?> type2;
        for (int i = 0; i < types1.length; i++) {
            type1 = types1[i];
            type2 = types2[i];
            if(isBasicType(type1) && isBasicType(type2)) {
                //原始类型和包装类型存在不一致情况
                if(BasicType.unWrap(type1) != BasicType.unWrap(type2)) {
                    return false;
                }
            }else if (false == type1.isAssignableFrom(type2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否为基本类型（包括包装类和原始类）
     *
     * @param clazz 类
     * @return 是否为基本类型
     */
    public static boolean isBasicType(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    /**
     * 是否为包装类型
     *
     * @param clazz 类
     * @return 是否为包装类型
     */
    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return BasicType.wrapperPrimitiveMap.containsKey(clazz);
    }

    public enum BasicType {
        BYTE, SHORT, INT, INTEGER, LONG, DOUBLE, FLOAT, BOOLEAN, CHAR, CHARACTER, STRING;

        /** 包装类型为Key，原始类型为Value，例如： Integer.class =》 int.class. */
        public static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new ConcurrentHashMap<>(8);
        /** 原始类型为Key，包装类型为Value，例如： int.class =》 Integer.class. */
        public static final Map<Class<?>, Class<?>> primitiveWrapperMap = new ConcurrentHashMap<>(8);

        static {
            wrapperPrimitiveMap.put(Boolean.class, boolean.class);
            wrapperPrimitiveMap.put(Byte.class, byte.class);
            wrapperPrimitiveMap.put(Character.class, char.class);
            wrapperPrimitiveMap.put(Double.class, double.class);
            wrapperPrimitiveMap.put(Float.class, float.class);
            wrapperPrimitiveMap.put(Integer.class, int.class);
            wrapperPrimitiveMap.put(Long.class, long.class);
            wrapperPrimitiveMap.put(Short.class, short.class);

            for (Map.Entry<Class<?>, Class<?>> entry : wrapperPrimitiveMap.entrySet()) {
                primitiveWrapperMap.put(entry.getValue(), entry.getKey());
            }
        }

        /**
         * 原始类转为包装类，非原始类返回原类
         * @param clazz 原始类
         * @return 包装类
         */
        public static Class<?> wrap(Class<?> clazz){
            if(null == clazz || false == clazz.isPrimitive()){
                return clazz;
            }
            Class<?> result = primitiveWrapperMap.get(clazz);
            return (null == result) ? clazz : result;
        }

        /**
         * 包装类转为原始类，非包装类返回原类
         * @param clazz 包装类
         * @return 原始类
         */
        public static Class<?> unWrap(Class<?> clazz){
            if(null == clazz || clazz.isPrimitive()){
                return clazz;
            }
            Class<?> result = wrapperPrimitiveMap.get(clazz);
            return (null == result) ? clazz : result;
        }
    }
}
