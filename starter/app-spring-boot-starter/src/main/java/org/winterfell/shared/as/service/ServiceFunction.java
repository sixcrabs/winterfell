package org.winterfell.shared.as.service;

import java.io.Serializable;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/12/26
 */
@FunctionalInterface
public interface ServiceFunction<T, R> extends Serializable {

 /**
  * Applies this function to the given argument.
  *
  * @param t the function argument
  * @return the function result
  */
 R apply(T t);
}