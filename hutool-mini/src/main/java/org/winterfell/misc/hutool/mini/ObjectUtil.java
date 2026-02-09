package org.winterfell.misc.hutool.mini;

import org.winterfell.misc.hutool.mini.collection.IterUtil;
import org.winterfell.misc.hutool.mini.io.FastByteArrayOutputStream;
import org.winterfell.misc.hutool.mini.io.IoUtil;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/8
 */
public final class ObjectUtil {

    /**
     * 比较两个对象是否相等。<br>
     * 相同的条件有两个，满足其一即可：<br>
     * <ol>
     * <li>obj1 == null &amp;&amp; obj2 == null</li>
     * <li>obj1.equals(obj2)</li>
     * </ol>
     * 1. obj1 == null &amp;&amp; obj2 == null 2. obj1.equals(obj2)
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     */
    public static boolean equal(Object obj1, Object obj2) {
        return (obj1 == obj2) || (obj1 != null && obj1.equals(obj2));
    }

    /**
     * 如果给定对象为{@code null}返回默认值
     *
     * <pre>
     * ObjectUtil.defaultIfNull(null, null)      = null
     * ObjectUtil.defaultIfNull(null, "")        = ""
     * ObjectUtil.defaultIfNull(null, "zz")      = "zz"
     * ObjectUtil.defaultIfNull("abc", *)        = "abc"
     * ObjectUtil.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
     * </pre>
     *
     * @param <T>          对象类型
     * @param object       被检查对象，可能为{@code null}
     * @param defaultValue 被检查对象为{@code null}返回的默认值，可以为{@code null}
     * @return 被检查对象为{@code null}返回默认值，否则返回原值
     * @since 3.0.7
     */
    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return isNull(object) ? defaultValue : object;
    }

    /**
     * 检查对象是否为null<br>
     * 判断标准为：
     *
     * <pre>
     * 1. == null
     * 2. equals(null)
     * </pre>
     *
     * @param obj 对象
     * @return 是否为null
     */
    public static boolean isNull(Object obj) {
        //noinspection ConstantConditions
        return null == obj || obj.equals(null);
    }

    /**
     * 检查对象是否不为null
     *
     * @param obj 对象
     * @return 是否为null
     */
    public static boolean isNotNull(Object obj) {
        return false == isNull(obj);
    }

    /**
     * 判断指定对象是否为空，支持：
     *
     * <pre>
     * 1. CharSequence
     * 2. Map
     * 3. Iterable
     * 4. Iterator
     * 5. Array
     * </pre>
     *
     * @param obj 被判断的对象
     * @return 是否为空，如果类型不支持，返回false
     * @since 4.5.7
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        }

        if (obj instanceof CharSequence) {
            return StringUtil.isEmpty((CharSequence) obj);
        } else if (obj instanceof Map) {
            return MapUtil.isEmpty((Map) obj);
        } else if (obj instanceof Iterable) {
            return IterUtil.isEmpty((Iterable) obj);
        } else if (obj instanceof Iterator) {
            return IterUtil.isEmpty((Iterator) obj);
        } else if (ArrayUtil.isArray(obj)) {
            return ArrayUtil.isEmpty(obj);
        }

        return false;
    }

    /**
     * 克隆对象<br>
     * 如果对象实现Cloneable接口，调用其clone方法<br>
     * 如果实现Serializable接口，执行深度克隆<br>
     * 否则返回<code>null</code>
     *
     * @param <T> 对象类型
     * @param obj 被克隆对象
     * @return 克隆后的对象
     */
    public static <T> T clone(T obj) {
        T result = ArrayUtil.clone(obj);
        if (null == result) {
            if (obj instanceof Cloneable) {
                result = ReflectUtil.invoke(obj, "clone", new Object[] {});
            } else {
                result = cloneByStream(obj);
            }
        }
        return result;
    }

    /**
     * 序列化后拷贝流的方式克隆<br>
     * 对象必须实现Serializable接口
     *
     * @param <T> 对象类型
     * @param obj 被克隆对象
     * @return 克隆后的对象
     * @throws UtilException IO异常和ClassNotFoundException封装
     */
    @SuppressWarnings("unchecked")
    public static <T> T cloneByStream(T obj) {
        if (null == obj || false == (obj instanceof Serializable)) {
            return null;
        }
        final FastByteArrayOutputStream byteOut = new FastByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
            out.flush();
            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
            return (T) in.readObject();
        } catch (Exception e) {
            throw new UtilException(e);
        } finally {
            IoUtil.close(out);
        }
    }
}
