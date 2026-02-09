package org.winterfell.misc.hutool.mini.convert;

import org.winterfell.misc.hutool.mini.ExceptionUtil;
import org.winterfell.misc.hutool.mini.StringUtil;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/1/19
 */
public class ConvertException extends RuntimeException{
    private static final long serialVersionUID = 4730597402855274362L;

    public ConvertException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(String messageTemplate, Object... params) {
        super(StringUtil.format(messageTemplate, params));
    }

    public ConvertException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ConvertException(Throwable throwable, String messageTemplate, Object... params) {
        super(StringUtil.format(messageTemplate, params), throwable);
    }
}
