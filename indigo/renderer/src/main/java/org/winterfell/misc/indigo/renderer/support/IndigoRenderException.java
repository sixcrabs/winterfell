package org.winterfell.misc.indigo.renderer.support;


import com.google.gson.JsonObject;
import org.winterfell.misc.hutool.mini.StringUtil;

/**
 * <p>
 * 文档渲染异常
 * </p>
 *
 * @author <a href="mailto:yingxiufeng@mlogcn.com">alex</a>
 * @version v1.0, 2020/3/23
 */
public class IndigoRenderException extends IndigoException {

    /**
     * 模板名
     */
    private String tplName;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public IndigoRenderException(String message, String tplName) {
        super(message);
        this.tplName = tplName;
    }

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public IndigoRenderException(Throwable cause, String tplName) {
        super(cause);
        this.tplName = tplName;
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public IndigoRenderException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public IndigoRenderException(Throwable cause) {
        super(cause);
    }

    /**
     * build message
     *
     * @return
     */
    @Override
    protected String buildMessage() {
        if (StringUtil.isBlank(tplName)) {
            return this.message;
        }
        JsonObject jsonObject = GsonUtil.make("msg", this.message);
        jsonObject.addProperty("tplName", tplName);
        return GsonUtil.toJsonString(jsonObject);
    }

    public IndigoRenderException(String msgTpl, Object... params) {
        super(StringUtil.format(msgTpl, params));
    }
}