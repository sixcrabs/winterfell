package org.winterfell.starter.javalin.support;

/**
 * <p>
 * 统一响应code
 * </p>
 *
 * @author <a href="mailto:yingxiufeng@mlogcn.com">alex</a>
 * @version v1.0, 2020/4/10
 */
public enum RespCode {

    /**
     * 成功响应
     */
    SUCCESS(0, "success"),

    /**
     *  接口层错误
     */
    API_ERROR(1000, "rest api error"),

    /**
     * 需要认证
     */
    AUTHORIZATION_NEED(1001, "need authorization"),

    /**
     * 参数无效
     */
    PARAMETERS_INVALID(1002, "invalid parameters"),

    /**
     * 权限不足
     */
    PERMISSION_NEED(1003, "need permission"),

    /**
     * 资源不存在
     */
    NOT_FOUND(1004, "resource not found"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(1005, "request timeout"),

    /**
     * 服务错误, 自定义错误
     */
    ERROR(9999, "unknown server error");

    /**
     * 错误编码
     */
    private int code;

    /**
     * 错误消息
     */
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    RespCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
