package org.winterfell.starter.javalin.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * <p>
 * rest 接口通用响应体
 * 说明：
 * <ul>
 *     <li>code=0: 正确返回</li>
 *     <li> 参考 {@link RespCode}</li>
 * </ul>
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/7/03
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unchecked")
public class Resp<T> implements Serializable {

    public static final Resp<String> OK = Resp.of(RespCode.SUCCESS);

    public static final Resp<String> FAIL = Resp.of(RespCode.ERROR);

    private int code;

    private String msg;

    private T data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    private Resp setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    private Resp setCode(int code) {
        this.code = code;
        return this;
    }

    private Resp setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * succeed result
     *
     * @param data
     * @param <T>    .
     * @return
     */
    public static <T> Resp<T> of(T data) {
        return Resp.of(RespCode.SUCCESS).setData(data);
    }

    /**
     * succeed
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Resp<T> succeed(T data) {
        return Resp.of(RespCode.SUCCESS).setData(data);
    }

    /**
     * 可以用 {@link RespCode} 表示的响应
     *
     * @param code
     * @return
     */
    public static Resp of(RespCode code) {
        return new Resp().setCode(code.getCode()).setMsg(code.getMsg());
    }

    /**
     * 错误码为 9999 ,自定义错误消息
     *
     * @param errorMsg
     * @return
     */
    public static Resp fail(String errorMsg) {
        return new Resp().setCode(RespCode.ERROR.getCode()).setMsg(errorMsg);
    }

    /**
     * 指定已知错误码 并添加详细信息
     *
     * @param code
     * @param detail
     * @return
     */
    public static Resp fail(RespCode code, String detail) {
        return Resp.of(code).setMsg(detail);
    }

    /**
     * 业务特定的无法在 {@link RespCode} 中体现的异常
     *
     * @param code     不能等于 0
     * @param errorMsg 错误消息
     * @return
     */
    public static Resp fail(int code, String errorMsg) {
        return new Resp().setCode(code).setMsg(errorMsg);
    }

    /**
     * exception
     *
     * @param ex
     * @return
     */
    public static Resp<String> of(Throwable ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return new Resp<String>().setCode(RespCode.ERROR.getCode()).setMsg(stringWriter.toString());
    }

    @JsonIgnore
    public boolean isSucceed() {
        return RespCode.SUCCESS.getCode() == code;
    }

    @Override
    public String toString() {
        return "Resp{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
