package org.winterfell.shared.as.advice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Collections;

/**
 * <p>
 * response 的默认实现
 * </p>
 *
 * @author Alex
 * @since 2025/10/10
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseImpl<T> implements Response<T> {

    private int code;

    private String message;

    private T data;

    protected ResponseImpl(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> of(int code, String message, T data) {
        return new ResponseImpl<>(code, message, data);
    }

    public static <T> Response<T> ofNull(int code, String message) {
        return new ResponseImpl<>(code, message, null);
    }

}