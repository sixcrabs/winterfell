package org.winterfell.misc.hutool.mini.collection;

import org.winterfell.misc.hutool.mini.ArrayUtil;
import org.winterfell.misc.hutool.mini.EnumerationIter;
import org.winterfell.misc.hutool.mini.TypeUtil;
import org.winterfell.misc.hutool.mini.UtilException;
import org.winterfell.misc.hutool.mini.convert.ConverterRegistry;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * <p>
 * .集合相关工具类，包括数组
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class CollUtil {



    /**
     * 将指定对象全部加入到集合中<br>
     * 提供的对象如果为集合类型，会自动转换为目标元素类型<br>
     *
     * @param <T> 元素类型
     * @param collection 被加入的集合
     * @param value 对象，可能为Iterator、Iterable、Enumeration、Array，或者与集合元素类型一致
     * @param elementType 元素类型，为空时，使用Object类型来接纳所有类型
     * @return 被加入集合
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Collection<T> addAll(Collection<T> collection, Object value, Type elementType) {
        if (null == collection || null == value) {
            return collection;
        }
        if (null == elementType) {
            // 元素类型为空时，使用Object类型来接纳所有类型
            elementType = Object.class;
        } else {
            final Class<?> elementRowType = TypeUtil.getClass(elementType);
            if (elementRowType.isInstance(value) && false == Iterable.class.isAssignableFrom(elementRowType)) {
                // 其它类型按照单一元素处理
                collection.add((T) value);
                return collection;
            }
        }

        Iterator iter;
        if (value instanceof Iterator) {
            iter = (Iterator) value;
        } else if (value instanceof Iterable) {
            iter = ((Iterable) value).iterator();
        } else if (value instanceof Enumeration) {
            iter = new EnumerationIter<>((Enumeration) value);
        } else if (ArrayUtil.isArray(value)) {
            iter = new ArrayIter<>(value);
        } else {
            throw new UtilException("Unsupported value type [] !", value.getClass());
        }

        final ConverterRegistry convert = ConverterRegistry.getInstance();
        while (iter.hasNext()) {
            try {
                collection.add((T) convert.convert(elementType, iter.next()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return collection;
    }
}
