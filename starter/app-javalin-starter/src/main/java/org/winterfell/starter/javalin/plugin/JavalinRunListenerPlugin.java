package org.winterfell.starter.javalin.plugin;

import org.winterfell.starter.javalin.JavalinApplicationRunListener;
import com.google.inject.Binding;
import com.google.inject.Injector;
import io.javalin.Javalin;
import io.javalin.core.plugin.Plugin;
import io.javalin.core.plugin.PluginLifecycleInit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * javalin 插件
 * <p>
 * 处理监听器插件
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/26
 */
public class JavalinRunListenerPlugin implements PluginLifecycleInit, Plugin {
    @Override
    public void apply(@NotNull Javalin javalin) {

    }

    @Override
    public void init(@NotNull Javalin app) {
        List<JavalinApplicationRunListener> runListeners = new ArrayList<>(1);
        Injector injector = app.attribute(Injector.class.getName());
        for (Binding<?> value : injector.getAllBindings().values()) {
            Class<?> rawType = value.getKey().getTypeLiteral().getRawType();
            if (JavalinApplicationRunListener.class.isAssignableFrom(rawType)) {
                runListeners.add((JavalinApplicationRunListener) value.getProvider().get());
            }
        }
        app.events(event -> {
            event.serverStarting(() -> runListeners.forEach(runListener -> runListener.starting(app)));
            event.serverStarted(() -> runListeners.forEach(runListener -> runListener.started(app)));
            event.serverStartFailed(() -> runListeners.forEach(runListener -> runListener.startFailed(app)));
            event.serverStopping(() -> runListeners.forEach(runListener -> runListener.stopped(app)));
            event.serverStopped(() -> runListeners.forEach(runListener -> runListener.stopped(app)));
            event.serverStopFailed(() -> runListeners.forEach(runListener -> runListener.stopFailed(app)));
        });

    }
}
