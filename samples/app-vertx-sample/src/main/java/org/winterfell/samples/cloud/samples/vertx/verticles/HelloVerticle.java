package org.winterfell.samples.cloud.samples.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.AllowForwardHeaders;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/12/15
 */
public class HelloVerticle extends AbstractVerticle {


    private final Logger logger = LoggerFactory.getLogger(HelloVerticle.class);
    private long counter = 1;


    /**
     * If your verticle does a simple, synchronous start-up then override this method and put your start-up
     * code in here.
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        Router router = Router.router(this.vertx);
        router.allowForward(AllowForwardHeaders.X_FORWARD);
        router.get("/").respond(ctx -> {
            logger.info("Request #{} from {}", counter++, ctx.request().remoteAddress().host());
            // 这里演示通过 shareData 传递 token
            vertx.sharedData().getLocalMap("tokenMap").put("token", "token_".concat(LocalDateTime.now().toString()));
            return Future.succeededFuture(new JsonObject().put("hello", counter));
        });
        router.get("/readFile").respond(ctx -> {
            return ctx.response()
                    .putHeader("Content-Type", "application/json")
                    .end(vertx.fileSystem()
                            .readFileBlocking("/Users/alex/Data/江苏省.json").toString());
        });
        vertx.createHttpServer().requestHandler(request -> {

                    // This handler gets called for each request that arrives on the server
                    HttpServerResponse response = request.response();
                    response.putHeader("content-type", "text/plain");

                    // Write to the response and end it
                    response.end("Hello World!");
                }).listen(8888)
                .onSuccess(server -> {
                    logger.info("Open http://localhost:8888/");
                });


    }

    /**
     * If your verticle has simple synchronous clean-up tasks to complete then override this method and put your clean-up
     * code in here.
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        System.out.println("stop....");
    }

    public static final Map<String, String> vIdRepo = new HashMap<>(1);

    public static void main(String[] args) {
        Vertx vertx1 = Vertx.vertx();
        DeploymentOptions deploymentOptions = new DeploymentOptions().setHa(true).setInstances(2);

        // 设置 config
        deploymentOptions.setConfig(new JsonObject().put("name", "Alex"));
        Future<String> future = vertx1.deployVerticle(HelloVerticle.class, deploymentOptions);
        future.onComplete(result -> {
            // id
            System.out.println(result.result());
            vIdRepo.put("default", result.result());
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> vertx1.undeploy(vIdRepo.get("default"))));
    }
}
