package org.winterfell.misc.indigo.renderer.support;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * <p>
 * wrapper
 * </p>
 *
 * @author alex
 * @version v1.0 2022/4/19
 */
public class ValueWrapper {

    /**
     * real value, not primitive
     */
    private Object value;

    private ValueWrapper(Object value) {
        this.value = value;
    }

    public static ValueWrapper of(@NonNull Object value) {
        return new ValueWrapper(value);
    }

    public Object getValue() {
        return value;
    }
}