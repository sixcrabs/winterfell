package org.winterfell.misc.hutool.mini;

import org.winterfell.misc.hutool.mini.convert.Convert;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/7
 */
public final class ReflectUtil {

    /** 构造对象缓存 */
    private static final SimpleCache<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE = new SimpleCache<>();
    /** 字段缓存 */
    private static final SimpleCache<Class<?>, Field[]> FIELDS_CACHE = new SimpleCache<>();
    /** 方法缓存 */
    private static final SimpleCache<Class<?>, Method[]> METHODS_CACHE = new SimpleCache<>();


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
            if (ClassUtil.isAllAssignableFrom(pts, parameterTypes)) {
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }

    /**
     * 获取字段值
     *
     * @param obj 对象
     * @param fieldName 字段名
     * @return 字段值
     * @throws UtilException 包装IllegalAccessException异常
     */
    public static Object getFieldValue(Object obj, String fieldName) throws UtilException {
        if (null == obj || StringUtil.isBlank(fieldName)) {
            return null;
        }
        return getFieldValue(obj, getField(obj.getClass(), fieldName));
    }

    /**
     * 获取字段值
     *
     * @param obj 对象
     * @param field 字段
     * @return 字段值
     * @throws UtilException 包装IllegalAccessException异常
     */
    public static Object getFieldValue(Object obj, Field field) throws UtilException {
        if (null == obj || null == field) {
            return null;
        }
        field.setAccessible(true);
        Object result = null;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            throw new UtilException(e, "IllegalAccess for {}.{}", obj.getClass(), field.getName());
        }
        return result;
    }
    /**
     * 获得一个类中所有字段列表，包括其父类中的字段
     *
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Field[] getFields(Class<?> beanClass) throws SecurityException {
        Field[] allFields = FIELDS_CACHE.get(beanClass);
        if (null != allFields) {
            return allFields;
        }

        allFields = getFieldsDirectly(beanClass, true);
        return FIELDS_CACHE.put(beanClass, allFields);
    }


    /**
     * 查找指定类中的所有字段（包括非public字段），也包括父类和Object类的字段， 字段不存在则返回<code>null</code>
     *
     * @param beanClass 被查找字段的类,不能为null
     * @param name 字段名
     * @return 字段
     * @throws SecurityException 安全异常
     */
    public static Field getField(Class<?> beanClass, String name) throws SecurityException {
        final Field[] fields = getFields(beanClass);
        if (ArrayUtil.isNotEmpty(fields)) {
            for (Field field : fields) {
                if ((name.equals(field.getName()))) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * 获得一个类中所有字段列表，直接反射获取，无缓存
     *
     * @param beanClass 类
     * @param withSuperClassFields 是否包括父类的字段列表
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFields) throws SecurityException {
        AssertUtil.notNull(beanClass);

        Field[] allFields = null;
        Class<?> searchType = beanClass;
        Field[] declaredFields;
        while (searchType != null) {
            declaredFields = searchType.getDeclaredFields();
            if (null == allFields) {
                allFields = declaredFields;
            } else {
                allFields = ArrayUtil.append(allFields, declaredFields);
            }
            searchType = withSuperClassFields ? searchType.getSuperclass() : null;
        }

        return allFields;
    }

    /**
     * 查找指定对象中的所有方法（包括非public方法），也包括父对象和Object类的方法
     *
     * <p>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     * </p>
     *
     * @param obj        被查找的对象，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param args       参数
     * @return 方法
     * @throws SecurityException 无访问权限抛出异常
     */
    public static Method getMethodOfObj(Object obj, String methodName, Object... args) throws SecurityException {
        if (null == obj || StringUtil.isBlank(methodName)) {
            return null;
        }
        return getMethod(obj.getClass(), methodName, ClassUtil.getClasses(args));
    }

    /**
     * 查找指定方法 如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        return getMethod(clazz, false, methodName, paramTypes);
    }

    /**
     * 查找指定方法 如果找不到对应的方法则返回{@code null}<br>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。<br>
     * 如果查找的方法有多个同参数类型重载，查找第一个找到的方法
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param ignoreCase 是否忽略大小写
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     * @since 3.2.0
     */
    public static Method getMethod(Class<?> clazz, boolean ignoreCase, String methodName, Class<?>... paramTypes) throws SecurityException {
        if (null == clazz || StringUtil.isBlank(methodName)) {
            return null;
        }

        final Method[] methods = getMethods(clazz);
        if (ArrayUtil.isNotEmpty(methods)) {
            for (Method method : methods) {
                if (StringUtil.equals(methodName, method.getName(), ignoreCase)
                        && ClassUtil.isAllAssignableFrom(method.getParameterTypes(), paramTypes)
                        //排除桥接方法，pr#1965@Github
                        && false == method.isBridge()) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 获得一个类中所有方法列表，包括其父类中的方法
     *
     * @param beanClass 类，非{@code null}
     * @return 方法列表
     * @throws SecurityException 安全检查异常
     */
    public static Method[] getMethods(Class<?> beanClass) throws SecurityException {
        AssertUtil.notNull(beanClass);
        return METHODS_CACHE.get(beanClass, () -> getMethodsDirectly(beanClass, true));
    }


    /**
     * 获得一个类中所有方法列表，直接反射获取，无缓存
     *
     * @param beanClass             类
     * @param withSuperClassMethods 是否包括父类的方法列表
     * @return 方法列表
     * @throws SecurityException 安全检查异常
     */
    public static Method[] getMethodsDirectly(Class<?> beanClass, boolean withSuperClassMethods) throws SecurityException {
        AssertUtil.notNull(beanClass);

        Method[] allMethods = null;
        Class<?> searchType = beanClass;
        Method[] declaredMethods;
        while (searchType != null) {
            declaredMethods = searchType.getDeclaredMethods();
            if (null == allMethods) {
                allMethods = declaredMethods;
            } else {
                allMethods = ArrayUtil.append(allMethods, declaredMethods);
            }
            searchType = withSuperClassMethods ? searchType.getSuperclass() : null;
        }

        return allMethods;
    }

    /**
     * 执行对象中指定方法
     * 如果需要传递的参数为null,请使用NullWrapperBean来传递,不然会丢失类型信息
     *
     * @param <T>        返回对象类型
     * @param obj        方法所在对象
     * @param methodName 方法名
     * @param args       参数列表
     * @return 执行结果
     * @throws UtilException IllegalAccessException包装
//     * @see NullWrapperBean
     * @since 3.1.2
     */
    public static <T> T invoke(Object obj, String methodName, Object... args) throws UtilException {
        AssertUtil.notNull(obj, "Object to get method must be not null!");
        AssertUtil.notBlank(methodName, "Method name must be not blank!");

        final Method method = getMethodOfObj(obj, methodName, args);
        if (null == method) {
            throw new UtilException("No such method: [{}] from [{}]", methodName, obj.getClass());
        }
        return invoke(obj, method, args);
    }

    /**
     * 执行方法
     *
     * <p>
     * 对于用户传入参数会做必要检查，包括：
     *
     * <pre>
     *     1、忽略多余的参数
     *     2、参数不够补齐默认值
     *     3、传入参数为null，但是目标参数类型为原始类型，做转换
     * </pre>
     *
     * @param <T>    返回对象类型
     * @param obj    对象，如果执行静态方法，此值为{@code null}
     * @param method 方法（对象方法或static方法都可）
     * @param args   参数对象
     * @return 结果
     * @throws UtilException 一些列异常的包装
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(Object obj, Method method, Object... args) throws UtilException {
        setAccessible(method);

        // 检查用户传入参数：
        // 1、忽略多余的参数
        // 2、参数不够补齐默认值
        // 3、通过NullWrapperBean传递的参数,会直接赋值null
        // 4、传入参数为null，但是目标参数类型为原始类型，做转换
        // 5、传入参数类型不对应，尝试转换类型
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Object[] actualArgs = new Object[parameterTypes.length];
        if (null != args) {
            for (int i = 0; i < actualArgs.length; i++) {
                if (i >= args.length || null == args[i]) {
                    // 越界或者空值
                    actualArgs[i] = ClassUtil.getDefaultValue(parameterTypes[i]);
                } else if (args[i] instanceof NullWrapperBean) {
                    //如果是通过NullWrapperBean传递的null参数,直接赋值null
                    actualArgs[i] = null;
                } else if (!parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                    //TODO: 对于类型不同的字段，尝试转换，转换失败则使用原对象类型
                    final Object targetValue = Convert.convert(parameterTypes[i], args[i]);
                    if (null != targetValue) {
                        actualArgs[i] = targetValue;
                    }
                } else {
                    actualArgs[i] = args[i];
                }
            }
        }

        if (method.isDefault()) {
            // 当方法是default方法时，尤其对象是代理对象，需使用句柄方式执行
            // 代理对象情况下调用method.invoke会导致循环引用执行，最终栈溢出
            return MethodHandleUtil.invokeSpecial(obj, method, args);
        }

        try {
            return (T) method.invoke(ClassUtil.isStatic(method) ? null : obj, actualArgs);
        } catch (Exception e) {
            throw new UtilException(e);
        }
    }


    /**
     * 设置方法为可访问（私有方法可以被外部调用）
     *
     * @param <T>              AccessibleObject的子类，比如Class、Method、Field等
     * @param accessibleObject 可设置访问权限的对象，比如Class、Method、Field等
     * @return 被设置可访问的对象
     * @since 4.6.8
     */
    public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        if (null != accessibleObject && !accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

    /**
     * 设置字段值
     *
     * @param obj 对象
     * @param fieldName 字段名
     * @param value 值，值类型必须与字段类型匹配，不会自动转换对象类型
     * @throws UtilException 包装IllegalAccessException异常
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws UtilException {
        AssertUtil.notNull(obj);
        AssertUtil.notBlank(fieldName);
        setFieldValue(obj, getField(obj.getClass(), fieldName), value);
    }


    /**
     * 设置字段值
     *
     * @param obj 对象
     * @param field 字段
     * @param value 值，值类型必须与字段类型匹配，不会自动转换对象类型
     * @throws UtilException UtilException 包装IllegalAccessException异常
     */
    public static void setFieldValue(Object obj, Field field, Object value) throws UtilException {
        AssertUtil.notNull(obj);
        AssertUtil.notNull(field);
        field.setAccessible(true);

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new UtilException(e, "IllegalAccess for {}.{}", obj.getClass(), field.getName());
        }
    }



    /**
     * 实例化对象
     *
     * @param <T> 对象类型
     * @param clazz 类
     * @param params 构造函数参数
     * @return 对象
     * @throws UtilException 包装各类异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz, Object... params) throws UtilException {
        if (ArrayUtil.isEmpty(params)) {
            if(Map.class.isAssignableFrom(clazz)) {
                //Map
                if(LinkedHashMap.class.isAssignableFrom(clazz)) {
                    return (T) MapUtil.newHashMap(true);
                }else {
                    return (T) MapUtil.newHashMap();
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
                throw new UtilException(e, "Instance class [{}] error!", clazz);
            }
        }

        final Class<?>[] paramTypes = ClassUtil.getClasses(params);
        final Constructor<?> constructor = getConstructor(clazz, paramTypes);
        if (null == constructor) {
            throw new UtilException("No Constructor matched for parameter types: [{}]", new Object[] { paramTypes });
        }
        try {
            return getConstructor(clazz, paramTypes).newInstance(params);
        } catch (Exception e) {
            throw new UtilException(e, "Instance class [{}] error!", clazz);
        }
    }

    /**
     * 尝试遍历并调用此类的所有构造方法，直到构造成功并返回
     *
     * @param <T> 对象类型
     * @param beanClass 被构造的类
     * @return 构造后的对象
     */
    public static <T> T newInstanceIfPossible(Class<T> beanClass) {
        AssertUtil.notNull(beanClass);
        try {
            return (T) beanClass.newInstance();
        } catch (Exception e) {
            // ignore
            // 默认构造不存在的情况下查找其它构造
        }

        final Constructor<T>[] constructors = getConstructors(beanClass);
        Class<?>[] parameterTypes;
        for (Constructor<T> constructor : constructors) {
            parameterTypes = constructor.getParameterTypes();
            if (0 == parameterTypes.length) {
                continue;
            }
            try {
                constructor.newInstance(ClassUtil.getDefaultValues(parameterTypes));
            } catch (Exception e) {
                // 构造出错时继续尝试下一种构造方式
                continue;
            }
        }
        return null;
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
     * 获得一个类中所有构造列表
     *
     * @param <T> 构造的对象类型
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T>[] getConstructors(Class<T> beanClass) throws SecurityException {
        AssertUtil.notNull(beanClass);
        Constructor<?>[] constructors = CONSTRUCTORS_CACHE.get(beanClass);
        if (null != constructors) {
            return (Constructor<T>[]) constructors;
        }

        constructors = getConstructorsDirectly(beanClass);
        return (Constructor<T>[]) CONSTRUCTORS_CACHE.put(beanClass, constructors);
    }

    /**
     * 获得一个类中所有字段列表，直接反射获取，无缓存
     *
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Constructor<?>[] getConstructorsDirectly(Class<?> beanClass) throws SecurityException {
        AssertUtil.notNull(beanClass);
        return beanClass.getDeclaredConstructors();
    }
}
