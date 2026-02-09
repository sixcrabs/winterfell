package org.winterfell.misc.hutool.mini;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/5/20
 */
public class NoSuchElementException extends RuntimeException {
    private static final long serialVersionUID = 6769829250639411880L;

    /**
     * Constructs a {@code NoSuchElementException} with {@code null}
     * as its error message string.
     */
    public NoSuchElementException() {
        super();
    }

    /**
     * Constructs a {@code NoSuchElementException} with the specified detail
     * message and cause.
     *
     * @param s     the detail message, or null
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method), or null
     * @since 15
     */
    public NoSuchElementException(String s, Throwable cause) {
        super(s, cause);
    }

    /**
     * Constructs a {@code NoSuchElementException} with the specified cause.
     * The detail message is set to {@code (cause == null ? null :
     * cause.toString())} (which typically contains the class and
     * detail message of {@code cause}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method)
     * @since 15
     */
    public NoSuchElementException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a {@code NoSuchElementException}, saving a reference
     * to the error message string {@code s} for later retrieval by the
     * {@code getMessage} method.
     *
     * @param   s   the detail message.
     */
    public NoSuchElementException(String s) {
        super(s);
    }
}
