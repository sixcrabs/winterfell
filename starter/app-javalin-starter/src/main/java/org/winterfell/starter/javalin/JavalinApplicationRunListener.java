package org.winterfell.starter.javalin;

import io.javalin.Javalin;

/**
 * <p>
 * javalin 应用启动监听器
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/26
 */
public interface JavalinApplicationRunListener {

    default void starting(Javalin javalin) {
    }

    default void stopping(Javalin javalin) {

    }

    default void started(Javalin javalin) {

    }

    default void startFailed(Javalin javalin) {

    }

    default void stopped(Javalin javalin) {

    }

    default void stopFailed(Javalin javalin) {

    }

}
