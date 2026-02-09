package org.winterfell.misc.zinc.support;

/**
 * <p>
 * 用于数据转换
 * TODO:
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/4/21
 */
public interface ZincDataConvertor<S,T> {

    T convert(S data);
}
