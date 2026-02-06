package org.winterfell.misc.hutool.mini.collection;

import java.io.Serializable;
import java.util.Enumeration;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/15
 */
public class EnumerationIter<E> implements IterableIter<E>, Serializable {
    private static final long serialVersionUID = 1L;

    private final Enumeration<E> e;

    /**
     * 构造
     * @param enumeration {@link Enumeration}对象
     */
    public EnumerationIter(Enumeration<E> enumeration) {
        this.e = enumeration;
    }

    @Override
    public boolean hasNext() {
        return e.hasMoreElements();
    }

    @Override
    public E next() {
        return e.nextElement();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
