package org.winterfell.samples.cloud.samples.vertx;

import org.winterfell.samples.cloud.samples.vertx.config.ZooConfig;
import io.github.sixcrabs.winterfell.starter.vertx.VertxApplication;
import io.github.sixcrabs.winterfell.starter.vertx.annotation.VertxBootApplication;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/4/26
 */
@VertxBootApplication(enableProperties = {ZooConfig.class}, basePackages = {"cn.piesat"})
public class VertxSampleApp {

    public static void main(String[] args) {
        VertxApplication.run(VertxSampleApp.class, true, args)
                .onComplete((evt) -> {
//                    VertxApplication application = evt.result();
                    // 这里启动 verticle
//                    application.getVertx().deployVerticle(new HelloVerticle());
                    System.out.println(" app started.");
                });
    }
}