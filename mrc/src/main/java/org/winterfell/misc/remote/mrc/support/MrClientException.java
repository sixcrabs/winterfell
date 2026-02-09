package org.winterfell.misc.remote.mrc.support;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/4/14
 */
public class MrClientException extends RuntimeException {

    public MrClientException() {
    }

    public MrClientException(String message) {
        super(message);
    }

    public MrClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public MrClientException(Throwable cause) {
        super(cause);
    }
}
