package org.winterfell.samples.cloud.samples.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.proxy.handler.ProxyHandler;
import io.vertx.httpproxy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * 测试 vertx  Reverse proxy 功能
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/25
 */
public class ProxyVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(ProxyVerticle.class);

    /**
     * If your verticle does a simple, synchronous start-up then override this method and put your start-up
     * code in here.
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        HttpServer proxyServer = vertx.createHttpServer();
        Router proxyRouter = Router.router(vertx);
        HttpClient proxyClient = vertx.createHttpClient();
        HttpProxy httpProxy1 = HttpProxy.reverseProxy(new ProxyOptions().setSupportWebSocket(true), proxyClient);
        HttpProxy httpProxy2 = HttpProxy.reverseProxy(new ProxyOptions().setSupportWebSocket(true), proxyClient);
        httpProxy2.origin(10030, "1.119.169.94");
        // 这里进行负载选择
///        httpProxy1.originSelector(request -> Future.succeededFuture(resolveOriginAddress(request)));

        httpProxy1.origin(8765, "localhost");
        httpProxy2.addInterceptor(new ProxyInterceptor() {
            @Override
            public Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
                ProxyRequest proxyRequest = context.request();
                logger.info(proxyRequest.headers().toString());
                // Continue the interception chain
                return context.sendRequest();
            }
        });
        proxyServer.requestHandler(proxyRouter);
        proxyServer.listen(8200);

        proxyRouter.route("/my").handler(ProxyHandler.create(httpProxy1));
        proxyRouter.routeWithRegex("/varok/*").handler(ProxyHandler.create(httpProxy2));

        logger.info("proxy available..");
    }

    private SocketAddress resolveOriginAddress(HttpServerRequest request) {
//        return SocketAddress.inetSocketAddress(8890, "localhost");
        System.out.println(request);
        return SocketAddress.inetSocketAddress(10030, "1.119.169.94");
    }
}
