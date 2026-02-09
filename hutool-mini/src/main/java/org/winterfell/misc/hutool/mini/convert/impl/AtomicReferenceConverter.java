package org.winterfell.misc.hutool.mini.convert.impl;

import org.winterfell.misc.hutool.mini.TypeUtil;
import org.winterfell.misc.hutool.mini.convert.AbstractConverter;
import org.winterfell.misc.hutool.mini.convert.ConverterRegistry;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class AtomicReferenceConverter extends AbstractConverter<AtomicReference> {

    @Override
    protected AtomicReference<?> convertInternal(Object value) {

        //尝试将值转换为Reference泛型的类型
        Object targetValue = null;
        final Type paramType = TypeUtil.getTypeArgument(AtomicReference.class);
        if(null != paramType){
            targetValue = ConverterRegistry.getInstance().convert(paramType, value);
        }
        if(null == targetValue){
            targetValue = value;
        }

        return new AtomicReference<>(targetValue);
    }

}
