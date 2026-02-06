package org.winterfell.samples.cloud.samples.vertx.verticles;

import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterfell.vertx.boot.VertxApplication;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/12/15
 */
public class SimpleVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(SimpleVerticle.class);

    /**
     * If your verticle does a simple, synchronous start-up then override this method and put your start-up
     * code in here.
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        // 这里通过 vertxApplication 的实例拿到 相关容器等
        Injector injector = VertxApplication.getInstance().getInjector();
        vertx.setPeriodic(4000, id -> {
//            logger.info("config data: {}", config());
//            logger.info("data from service: {}", injector.getInstance(ZooService.class).sayHi("Alex"));
            // 这里演示通过 shareData 传递 token
            Object val = vertx.sharedData().getLocalMap("tokenMap").get("token");
            logger.warn("val is : {}", val);
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
        logger.error("stopping...");
    }

    public static void main(String[] args) {

        Vertx vertx1 = Vertx.vertx();
        vertx1.deployVerticle(SimpleVerticle.class, new DeploymentOptions().setHa(true));
    }
}
