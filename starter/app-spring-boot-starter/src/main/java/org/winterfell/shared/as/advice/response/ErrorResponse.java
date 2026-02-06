package org.winterfell.shared.as.advice.response;

import lombok.Getter;

/**
 * <p>
 * 统一响应code
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/4/10
 */
@Getter
public enum ErrorResponse {

    /**
     * 接口层错误
     */
    API_ERROR(1000, "Api error"),

    /**
     * 需要认证
     */
    AUTHORIZATION_NEED(1001, "请求需要身份认证"),

    /**
     *  参数无效
     */
    PARAMETERS_INVALID(1002, "请求参数无效"),

    /**
     * 权限不足
     */
    PERMISSION_NEED(1003, "请求需要身份权限"),

    /**
     * 资源不存在
     */
    NOT_FOUND(1004, "资源不存在"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(1005, "请求超时"),

    /**
     * 请求被限流
     */
    REQUEST_RATE_LIMITED(1006, "超出访问频率，请求被限制");

    private final int code;

    private final String msg;

    ErrorResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
