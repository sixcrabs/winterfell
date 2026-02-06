package org.winterfell.starter.javalin.support;

import org.winterfell.misc.hutool.mini.StringUtil;

/**
 * <p>
 * web 接口统一异常
 *
 * </p>
 *
 * @author alex
 * @version v1.0 2022/7/30
 */
public class WebApiException extends Exception {


    public WebApiException() {
    }

    public WebApiException(String message) {
        super(message);
    }

    public WebApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebApiException(Throwable cause) {
        super(cause);
    }

    public WebApiException(String msgTpl, Object... params) {
        super(StringUtil.format(msgTpl, params));
    }
}
