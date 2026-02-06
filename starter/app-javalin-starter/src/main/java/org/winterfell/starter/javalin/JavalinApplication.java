package org.winterfell.starter.javalin;

import org.winterfell.misc.hutool.mini.ClassUtil;
import org.winterfell.misc.hutool.mini.ReflectUtil;
import org.winterfell.starter.javalin.annotation.JavalinComponentScan;
import org.winterfell.starter.javalin.annotation.RequestController;
import org.winterfell.starter.javalin.annotation.RequestMapping;
import org.winterfell.starter.javalin.plugin.JavalinConfigProviderPlugin;
import org.winterfell.starter.javalin.plugin.JavalinInjectPlugin;
import org.winterfell.starter.javalin.plugin.JavalinRunListenerPlugin;
import org.winterfell.starter.javalin.support.JavalinPropertiesResolver;
import org.winterfell.starter.javalin.support.Resp;
import org.winterfell.starter.javalin.support.WebApiException;
import com.google.inject.Injector;
import io.javalin.Javalin;
import io.javalin.core.compression.CompressionStrategy;
import io.javalin.core.plugin.Plugin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.http.HandlerType;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.ReDocOptions;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * javalin 应用启动类
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/26
 */
public class JavalinApplication {

    public static final Logger LOG = LoggerFactory.getLogger(JavalinApplication.class);

    private final Class<?> primarySource;

    /**
     * 应用自定义plugin
     */
    private final Set<Plugin> pluginSet;


    private JavalinApplication(Class<?> primarySource) {
        this.primarySource = primarySource;
        this.pluginSet = new HashSet<>(1);
    }

    public static Javalin run(Class<?> primarySource, String... args) {
        return new JavalinApplication(primarySource).run(args);
    }

    public static JavalinApplication initialize(Class<?> primarySource, Plugin... plugins) {
        JavalinApplication application = new JavalinApplication(primarySource);
        application.pluginSet.addAll(Arrays.asList(plugins));
        return application;
    }

    public Javalin run(String... args) {

        JavalinComponentScan annotation = this.primarySource.getAnnotation(JavalinComponentScan.class);
        String[] basePackages = annotation.value().length == 0 ? new String[]{"cn.piesat.v"} : annotation.value();

        // 1. 初始化应用配置 注册插件等
        final Javalin app = Javalin.create(config -> {
            config.enableWebjars();
            config.enableCorsForAllOrigins();
            config.enableDevLogging();
            config.compressionStrategy(CompressionStrategy.GZIP);
            config.defaultContentType = "application/json";
            config.showJavalinBanner = false;

            // load plugins
            LOG.info("[javalin] plugin register......");

            config.registerPlugin(new RouteOverviewPlugin("/routes"));
            JavalinPropertiesResolver.INSTANCE.resolveProperties(basePackages);
            if(JavalinPropertiesResolver.INSTANCE.getAppConfig().getOpenapi().isEnabled()) {
                config.registerPlugin(new OpenApiPlugin(getOpenApiOptions()));
            } else {
                LOG.warn("[javalin] openapi is disabled");
            }
            config.registerPlugin(new JavalinConfigProviderPlugin());
            if (!this.pluginSet.isEmpty()) {
                for (Plugin plugin : this.pluginSet) {
                    config.registerPlugin(plugin);
                }
            }
            config.registerPlugin(new JavalinInjectPlugin(basePackages));
            config.registerPlugin(new JavalinRunListenerPlugin());

        });

        // 2. 定义handlerMapping
        Injector injector = app.attribute(Injector.class.getName());
        final Set<Class<?>> controllerClasses = new HashSet<>(1);
        String pkg = app.attribute("controller-packages");
        Arrays.stream(pkg.split(",")).forEach(p -> controllerClasses.addAll(ClassUtil.scanPackageByAnnotation(p, RequestController.class)));

        app.exception(WebApiException.class, (exception, ctx) -> {
            ctx.json(Resp.fail(exception.getLocalizedMessage()));
        });

        app.routes(() -> {
            for (Class<?> controllerClass : controllerClasses) {
                Object instance = injector.getInstance(controllerClass);
                Method[] methods = ReflectUtil.getMethodsDirectly(controllerClass, false);
                List<Method> handlerMethods = Arrays.stream(methods).filter(method -> method.isAnnotationPresent(RequestMapping.class)).collect(Collectors.toList());
                String rootPath = controllerClass.getAnnotation(RequestController.class).value();
                handlerMethods.forEach(handlerMethod -> {
                    String path = rootPath + handlerMethod.getAnnotation(RequestMapping.class).value();
                    HandlerType type = handlerMethod.getAnnotation(RequestMapping.class).method();
                    if (type.isHttpMethod()) {
                        app.addHandler(type, path, ctx -> {
                            try {
                                Object val = handlerMethod.invoke(instance, ctx);
                                if (val != null) {
                                    // 统一响应格式
                                    if (Resp.class.isAssignableFrom(val.getClass())) {
                                        ctx.json(val);
                                    } else {
                                        ctx.json(Resp.of(val));
                                    }
                                }
                            } catch (Exception e) {
                                throw new WebApiException(e.getCause().getLocalizedMessage());
                            }
                        });
                    }

                });
            }
        });

        // 3. 注册回调事件监听
        app.events(event -> {
            event.serverStarted(() ->
                    Runtime.getRuntime().addShutdownHook(new Thread(app::stop)));
            event.handlerAdded(handlerMetaInfo -> {
                LOG.debug(handlerMetaInfo.getPath());
            });
        });
        JavalinAppConfig appConfig = app.attribute(JavalinAppConfig.class.getName());
        // 4. 根据配置信息 启动server
        if (appConfig == null) {
            LOG.error("app config is null");
        }
        assert appConfig != null;
        app.start(appConfig.getServer().getPort());
        return app;
    }

    /**
     * 设置 swagger 访问信息
     *
     * @return
     */
    private static OpenApiOptions getOpenApiOptions() {
        JavalinAppConfig appConfig = JavalinPropertiesResolver.INSTANCE.getAppConfig();
        JavalinAppConfig.OpenApiProperties openapi = appConfig.getOpenapi();
        Info applicationInfo = new Info()
                .version(openapi.getInfo().getVersion())
                .title(openapi.getInfo().getTitle())
                .description(openapi.getInfo().getDescription());
        OpenApiOptions openApiOptions = new OpenApiOptions(applicationInfo)
                .path("/swagger-docs")
                .swagger(new SwaggerOptions("/swagger-ui").title("My Main Api Doc"))
                .reDoc(new ReDocOptions("/redoc").title("My ReDoc Documentation"))
                .disableCaching();
        openapi.getScanPackages().forEach(openApiOptions::activateAnnotationScanningFor);
        openapi.getIgnorePaths().forEach(openApiOptions::ignorePath);
        return openApiOptions;
    }
}
