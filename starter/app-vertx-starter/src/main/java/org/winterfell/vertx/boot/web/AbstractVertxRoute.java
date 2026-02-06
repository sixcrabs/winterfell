package org.winterfell.vertx.boot.web;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.*;

/**
 * <p>
 * 封装处理request response的方法
 * </p>
 *
 * @author alex
 * @version v1.0
 */
public abstract class AbstractVertxRoute {

    /**
     * 跳转
     *
     * @param context
     * @param newUrl
     */
    protected void redirect(RoutingContext context, String newUrl) {
        context.response().setStatusCode(HttpResponseStatus.FOUND.code()).putHeader("Location", newUrl);
    }

    /**
     * output json
     *
     * @param res
     * @param json
     */
    protected void renderJson(HttpServerResponse res, String json) {
        res.putHeader("content-type", "application/json; charset=utf-8").end(json);
    }

    /**
     * 返回成功的标准的格式
     *
     * @param res
     * @param data
     */
    protected void renderSuccess(HttpServerResponse res, Object data) {
        renderJson(res, normalObj(0, "success", data).encode());
    }

    protected void renderSuccess(HttpServerResponse res) {
        renderJson(res, normalObj(0, "success").encode());
    }

    /**
     * 返回失败的标准格式
     *
     * @param res
     * @param code error code
     * @param msg  error msg
     */
    protected void renderFailure(HttpServerResponse res, int code, String... msg) {
        renderJson(res, normalObj(code, String.join("", msg)).encode());
    }

    /**
     * 返回失败的标准格式
     *
     * @param res
     * @param throwable
     */
    protected void renderFailure(HttpServerResponse res, Throwable throwable) {
        renderJson(res, normalObj(500, throwable.getLocalizedMessage()).encode());
    }

    private JsonObject normalObj(Integer code, String msg, Object data) {
        return new JsonObject().put("code", Objects.isNull(code) ? 0 : code)
                .put("message", msg).put("data", data);
    }

    private JsonObject normalObj(Integer code, String msg) {
        return new JsonObject().put("code", Objects.isNull(code) ? 0 : code)
                .put("message", msg);
    }

    /**
     * 从请求中获取参数
     *
     * @param req      请求
     * @param name     参数名
     * @param defValue 默认值
     * @return
     */
    protected String param(HttpServerRequest req, String name, String... defValue) {
        String val = req.getParam(name);
        return (val != null) ? val : (defValue.length > 0) ? defValue[0] : null;
    }

    protected int param(HttpServerRequest req, String name, int defValue) {
        String val = req.getParam(name);
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException ex) {
            return defValue;
        }
    }

    private Map<String, Object> params(HttpServerRequest request) {
        Map<String, Object> params = new HashMap<>(2);
        MultiMap mm = request.params();
        for (String name : mm.names()) {
            List<String> values = mm.getAll(name);
            if (values.size() == 1) {
                params.put(name, values.get(0));
            } else {
                params.put(name, values.toArray(new String[0]));
            }
        }
        return Collections.unmodifiableMap(params);
    }

    /**
     * 参数转换为 json object
     *
     * @param request
     * @return
     */
    protected JsonObject paramsToJson(HttpServerRequest request) {
        return JsonObject.mapFrom(params(request));
    }

    protected void error(HttpServerResponse res, int code, String... msg) {
        res.setStatusCode(code);
        if (msg != null && msg.length > 0) {
            res.setStatusMessage(String.join("", msg));
        }
    }


}
