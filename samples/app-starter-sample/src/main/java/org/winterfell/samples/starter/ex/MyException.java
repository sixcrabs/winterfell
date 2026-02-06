package org.winterfell.samples.starter.ex;

import org.winterfell.shared.as.advice.ex.ErrorAdvice;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/11
 */
@ErrorAdvice(code = 10001, message = "自定义异常")
public class MyException extends RuntimeException {

    public MyException(String msg) {
        super(msg);
    }

    public MyException() {
    }
}
