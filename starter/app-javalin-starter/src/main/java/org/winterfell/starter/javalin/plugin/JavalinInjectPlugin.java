package org.winterfell.starter.javalin.plugin;

import org.winterfell.misc.hutool.mini.ClassUtil;
import org.winterfell.starter.javalin.annotation.JavalinComponent;
import org.winterfell.starter.javalin.annotation.JavalinProperties;
import org.winterfell.starter.javalin.annotation.RequestController;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.internal.SingletonScope;
import io.javalin.Javalin;
import io.javalin.core.plugin.Plugin;
import io.javalin.core.plugin.PluginLifecycleInit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *  javalin 插件
 * <p>
 * 依赖注入 plugin
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/26
 */
public class JavalinInjectPlugin implements PluginLifecycleInit, Plugin {

    public static final Logger LOG = LoggerFactory.getLogger(JavalinInjectPlugin.class);

    private final String[] scanPackages;

    public JavalinInjectPlugin(String[] scanPackages) {
        this.scanPackages = scanPackages;
    }

    @Override
    public void apply(@NotNull Javalin javalin) {

    }

    @Override
    public void init(@NotNull Javalin app) {
        List<AbstractModule> modules = new ArrayList<>(1);
        List<String> controllerPkg = new ArrayList<>(1);
        Set<Class<?>> injectableClasses = new HashSet<>(1);

        // 扫描所有需要注入的类 以及 controller
        if (scanPackages != null) {
            Arrays.stream(scanPackages).forEach(pkg -> {
                Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation(pkg, RequestController.class);
                if (!classSet.isEmpty()) {
                    controllerPkg.add(pkg);
                }
                injectableClasses.addAll(ClassUtil.scanPackageByAnnotation(pkg, JavalinComponent.class));
                injectableClasses.addAll(classSet);
                injectableClasses.addAll(ClassUtil.scanPackageByAnnotation(pkg, JavalinProperties.class));

                injectableClasses.forEach(clazz -> {
                    if (!clazz.isAnnotation()) {
                        modules.add(new AbstractModule() {
                            @Override
                            protected void configure() {
                                super.configure();
                                if (app.attribute(clazz.getName()) != null) {
                                    // 注入已经在app中挂载的实例，比如 AppConfig
                                    bind(clazz).toInstance(app.attribute(clazz.getName()));
                                } else {
                                    // TBD 默认单例
                                    bind(clazz).in(new SingletonScope());
                                }
                            }
                        });
                    }
                });
            });
        }
        Injector injector = Guice.createInjector(modules);
        // 放入全局变量
        app.attribute(Injector.class.getName(), injector);
        app.attribute("controller-packages", String.join(",", controllerPkg));

    }
}
