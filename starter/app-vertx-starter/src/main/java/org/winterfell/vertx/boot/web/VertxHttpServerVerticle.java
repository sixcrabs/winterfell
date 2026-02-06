package org.winterfell.vertx.boot.web;

import org.winterfell.vertx.boot.annotation.VertxHttpRequest;
import org.winterfell.vertx.boot.annotation.VertxRoute;
import com.google.inject.Injector;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.AllowForwardHeaders;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterfell.vertx.boot.VertxProperties;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统默认发布的 verticle
 * 用于实现 web 请求处理
 * </p>
 *
 * @author alex
 * @version v1.0
 */
public class VertxHttpServerVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(VertxHttpServerVerticle.class);

    private final Injector injector;

    private final VertxProperties.Server serverProperties;

    private final Set<Class<?>> routeClasses;

    private Router router;

    private HttpServer server;

    private static final String VERSION = "Vertx-Boot/1.0";

    private final boolean accessLog = true;

    public VertxHttpServerVerticle(Injector injector, VertxProperties.Server serverProperties, Set<Class<?>> routeClasses) {
        this.injector = injector;
        this.serverProperties = serverProperties;
        this.routeClasses = routeClasses;
    }


    /**
     * 开启http server
     */
    @Override
    public void start() {
        this.server = this.vertx.createHttpServer();
        this.router = Router.router(this.vertx);
        router.allowForward(AllowForwardHeaders.X_FORWARD);
        router.route().handler(context -> {
            this.writeGlobalHeaders(context.response());
            context.next();
        });
        router.route().handler(BodyHandler.create().setHandleFileUploads(true));
        /// static files
        ///        router.routeWithRegex(pattern_static_file).blockingHandler(new AutoContentTypeStaticHandler(), false);
        routeClasses.forEach(this::resolveRoute);
//        InetSocketAddress address = (serverProperties.getHost() == null) ? new InetSocketAddress(this.serverProperties.getPort()) :
//                new InetSocketAddress(this.serverProperties.getHost(), this.serverProperties.getPort());
//        SocketAddress.inetSocketAddress(address)
        this.server.requestHandler(router);
        this.server.listen(this.serverProperties.getPort())
                .onFailure(throwable -> log.error("[vertx-boot] http server start failed", throwable))
                .onSuccess(server -> log.info("[vertx-boot] vertx http server started, listen on {}:{}!",
                        (this.serverProperties.getHost() == null) ? "*" : this.serverProperties.getHost(), this.serverProperties.getPort()));
    }

    /**
     * If your verticle has simple synchronous clean-up tasks to complete then override this method and put your clean-up
     * code in here.
     * 停止 server
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        if (this.server != null) {
            this.server.close();
        }
    }

    private Map<String, Object> routeInstances = new HashMap<>(16);

    /**
     * 解析对应的class 的方法到 路由实例里
     *
     * @param routeClazz
     */
    @SuppressWarnings("rawtypes")
    private void resolveRoute(@Nonnull Class<?> routeClazz) {
        VertxRoute routeAnno = routeClazz.getAnnotation(VertxRoute.class);
        String basePath = routeAnno.value();
        boolean isBlocking = routeAnno.blocking();
        // TBD: 先注入一个route class实例
        routeInstances.put(routeClazz.getName(), injector.getInstance(routeClazz));
        // 解析所有的 method
        for (Method actionMethod : routeClazz.getDeclaredMethods()) {
            if (Modifier.isPublic(actionMethod.getModifiers())) {
                VertxHttpRequest requestAnno = actionMethod.getAnnotation(VertxHttpRequest.class);
                if (Objects.isNull(requestAnno)) {
                    continue;
                }
                Class[] ptypes = actionMethod.getParameterTypes();
                if (ptypes.length > 1) {
                    continue;
                }
                // 方法只能有一个参数 context
                if (ptypes.length == 1 && !ptypes[0].equals(RoutingContext.class)) {
                    continue;
                }
                Route route = router.route(basePath.concat(requestAnno.value()));
                boolean requestBlocking = requestAnno.blocking();
                if (isBlocking) {
                    requestBlocking = true;
                }
                boolean finalRequestBlocking = requestBlocking;
                Arrays.stream(requestAnno.method()).forEach(
                        requestMethod -> {
                            route.method(requestMethod.toHttpMethod());
                            if (finalRequestBlocking) {
                                route.blockingHandler(context -> requestHandler(actionMethod, context));
                            } else {
                                route.handler(context -> requestHandler(actionMethod, context));
                            }
                        });
            }
        }
    }

    /**
     * 处理请求
     *
     * @param actionMethod 请求对应的方法
     * @param context
     */
    @SuppressWarnings("unchecked")
    private void requestHandler(Method actionMethod, RoutingContext context) {
        long ct = System.currentTimeMillis();
        try {
            boolean isStatic = Modifier.isStatic(actionMethod.getModifiers());
            Class<?> actionClass = actionMethod.getDeclaringClass();
            HttpServerResponse response = context.response();
            Object invokeResult = null;
            try {
                //injector.getInstance(actionClass);
                Object targetObject = isStatic ? actionClass : routeInstances.get(actionClass.getName());
                switch (actionMethod.getParameterCount()) {
                    case 0:
                        invokeResult = actionMethod.invoke(targetObject);
                        break;
                    case 1:
                        invokeResult = actionMethod.invoke(targetObject, context);
                        break;
                    default:
                        throw new IllegalArgumentException(actionMethod.getName());
                }
            } catch (InvocationTargetException e) {
                log.error("Failed to invoke " + context.request().uri(), e.getCause());
                sendError(response, HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), e.getCause().getMessage());
            } catch (IllegalArgumentException e) {
                sendError(response, HttpResponseStatus.NOT_ACCEPTABLE.code());
            } catch (IllegalAccessException e) {
                sendError(response, HttpResponseStatus.FORBIDDEN.code());
            } catch (Throwable t) {
                log.error("Failed to invoke " + context.request().uri(), t);
                sendError(response, HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), t.getMessage());
            }
            if (invokeResult != null) {
                // TODO： 这里需要支持json格式包装等
                sendData(response, invokeResult);
            }

        } finally {
            HttpServerResponse res = context.response();
            if (!res.ended()) {
                res.end();
            }
            if (!res.closed()) {
                context.request().connection().close();
            }
        }
        writeAccessLog(context, System.currentTimeMillis() - ct);
    }

    private void sendError(HttpServerResponse res, int code, String... msg) {
        res.setStatusCode(code);
        if (msg != null && msg.length > 0) {
            res.setStatusMessage(String.join("", msg));
        }
        res.end();
    }


    /**
     * 发送数据
     *
     * @param response
     * @param data
     */
    private void sendData(HttpServerResponse response, @Nonnull Object data) {
        if (isPrimitiveOrString(data.getClass())) {
            response.putHeader("content-type", "text/plain").end(String.valueOf(data));
        } else {
            response.putHeader("content-type", "application/json; charset=utf-8")
                    .end(new JsonObject().put("code", 0).put("message", "success").put("data", data).encode());
        }
    }


    /**
     * 全局 headers
     *
     * @param res
     */
    private void writeGlobalHeaders(HttpServerResponse res) {
        res.putHeader("server", VERSION);
        res.putHeader("date", new Date().toString());
    }

    /**
     * 记录日志
     *
     * @param context
     * @param time
     */
    private void writeAccessLog(RoutingContext context, long time) {
        if (accessLog) {
            HttpServerRequest req = context.request();
            String ua = req.getHeader("User-Agent");
            if (ua == null) {
                ua = "-";
            }
            String params = req.params().entries().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(","));
            String msg = String.format("%s - \"%s %s %s %s\" %d %d - %dms - \"%s\"",
                    req.remoteAddress().hostAddress(),
                    req.method().name(),
                    req.uri(),
                    params,
                    context.body().asString(),
                    req.response().getStatusCode(),
                    req.response().bytesWritten(),
                    time,
                    ua);
            log.info(msg);
        }
    }

    private static final Class<?>[] PRIMITIVE_TYPES = {int.class, long.class, short.class,
            float.class, double.class, byte.class, boolean.class, char.class, Integer.class, Long.class,
            Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class};

    private boolean isPrimitiveOrString(Class<?> type) {
        if (String.class.equals(type)) {
            return true;
        }
        for (Class<?> standardPrimitive : PRIMITIVE_TYPES) {
            if (standardPrimitive.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }


}
