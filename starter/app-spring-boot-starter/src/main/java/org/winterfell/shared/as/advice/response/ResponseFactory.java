package org.winterfell.shared.as.advice.response;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/10
 */
public interface ResponseFactory {

    <T> Response<T> create(boolean success);

    <T> Response<T> createSuccess(T data);

    <T> Response<T> createFail();

    <T> Response<T> createFail(String message);

    <T> Response<T> createFail(int code, String message);
}