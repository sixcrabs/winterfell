package org.winterfell.shared.as.advice.ex;

import org.winterfell.shared.as.advice.response.ErrorResponse;
import lombok.Getter;

/**
 * <p>
 * web api exception
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/4/10
 */
@Getter
public class WebApiException extends RuntimeException {

    private final int code;

    private final String msg;

    public WebApiException() {
        this(ErrorResponse.API_ERROR.getCode(), ErrorResponse.API_ERROR.getMsg());
    }

    public WebApiException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public WebApiException(String msg) {
        this.code = ErrorResponse.API_ERROR.getCode();
        this.msg = msg;
    }

    public WebApiException(Throwable cause) {
        this(ErrorResponse.API_ERROR.getCode(), cause.getLocalizedMessage());
    }
}
