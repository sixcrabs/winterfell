package org.winterfell.shared.as.advice.response;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/9
 */
public interface Response<T> {

    void setData(T data);

    T getData();

    void setCode(int code);

    int getCode();

    void setMessage(String message);

    String getMessage();
}
