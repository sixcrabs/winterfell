package org.winterfell.misc.hutool.mini.collection;

import java.util.Iterator;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/15
 */
public interface IterableIter<T> extends Iterable<T>, Iterator<T> {
    @Override
    default Iterator<T> iterator() {
        return this;
    }
}
