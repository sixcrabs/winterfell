package org.winterfell.vertx.boot;

import org.winterfell.misc.hutool.mini.ClassUtil;
import org.winterfell.misc.hutool.mini.MapUtil;
import org.winterfell.misc.hutool.mini.StringUtil;
import org.winterfell.vertx.boot.annotation.VertxBootApplication;
import org.winterfell.vertx.boot.annotation.VertxComponent;
import org.winterfell.vertx.boot.annotation.VertxConfigurationProperties;
import org.winterfell.vertx.boot.support.IocClassModule;
import org.winterfell.vertx.boot.support.IocInterfaceModule;
import com.google.common.base.Stopwatch;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.impl.future.PromiseImpl;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterfell.vertx.boot.support.IocInstanceModule;
import org.winterfell.vertx.boot.support.ReadOnlySystemAttributesMap;
import org.winterfell.vertx.boot.web.VertxHttpServerVerticle;
import org.winterfell.vertx.boot.annotation.VertxRoute;

import javax.annotation.Nullable;
import java.io.File;
import java.security.AccessControlException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Vertx Application
 * </p>
 *
 * @author alex
 * @version v1.0
 */
public class VertxApplication {

    private static final Logger logger = LoggerFactory.getLogger(VertxApplication.class);
    public static final String BANNER = "  _   __        __        ___            __ \n" +
            " | | / /__ ____/ /___ __ / _ )___  ___  / /_\n" +
            " | |/ / -_) __/ __/\\ \\ // _  / _ \\/ _ \\/ __/\n" +
            " |___/\\__/_/  \\__//_\\_\\/____/\\___/\\___/\\__/ \n";
    private static final String DEFAULT_NAMES = "application";

    /**
     * The "config location" property name.
     * 运行时/系统变量 key 值用于添加配置文件位置
     */
    private static final String CONFIG_LOCATION_PROPERTY = "vertx.config.location";

    /**
     * The "active profiles" property name.
     * 运行时/系统变量 key 值用于修改当前激活的 profile
     */
    private static final String ACTIVE_PROFILES_PROPERTY = "vertx.profiles.active";

    private final Class<?> primarySource;
    private Injector injector;
    private Vertx vertx;
    private Stopwatch stopwatch;
    private PromiseImpl<VertxApplication> promise;
    private VertxProperties vertxProperties;

    private static VertxApplication instance = null;
    /**
     * 默认启动一个 web 服务
     */
    private static boolean shouldStartWeb = true;

    private VertxApplication(Class<?> primarySource) {
        this.primarySource = primarySource;
    }

    public static VertxApplication getInstance() {
        return instance;
    }

    public Injector getInjector() {
        return injector;
    }

    public Vertx getVertx() {
        return vertx;
    }

    /**
     * 读取配置、进行依赖注入、构造一个 vertx 应用
     *
     * @param primarySource 启动类
     * @param args          启动参数
     * @return future
     */
    public static Future<VertxApplication> run(Class<?> primarySource, String... args) {
        instance = new VertxApplication(primarySource);
        return instance.run(args);
    }

    /**
     * 读取配置、进行依赖注入、构造一个 vertx 应用
     *
     * @param primarySource 启动类
     * @param withWeb       是否启动 web
     * @param args          启动参数
     * @return future
     */
    public static Future<VertxApplication> run(Class<?> primarySource, boolean withWeb, String... args) {
        shouldStartWeb = withWeb;
        instance = new VertxApplication(primarySource);
        return instance.run(args);
    }

    /**
     * 1. 准备环境 读取 args 以及环境变量 得到 profile.active 等
     * 2. 开始读取 配置文件 并映射到具体配置类中
     * 3. 初始化收集 ioc
     * 4. 获取 vertx 实例
     * 5. 初始化 http server 并发布
     * 6. 触发回调
     *
     * @param args 应用启动参数
     * @return future
     */
    private Future<VertxApplication> run(String... args) {
        stopwatch = Stopwatch.createStarted();
        promise = new PromiseImpl<>();
        Map<String, Object> sysEnvironment = getSystemEnvironment();
        Map<String, Object> sysProperties = getSystemProperties();

        ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
        // 激活的profile值 当属性生效 则 application.yml 中忽略
        String activeProfile = sysEnvironment.containsKey(ACTIVE_PROFILES_PROPERTY) ? MapUtil.getStr(sysEnvironment, ACTIVE_PROFILES_PROPERTY) :
                MapUtil.getStr(sysProperties, ACTIVE_PROFILES_PROPERTY, "");
        // 配置位置  当属性生效,则 application.yml 中忽略
        String configLocation = sysEnvironment.containsKey(CONFIG_LOCATION_PROPERTY) ? MapUtil.getStr(sysEnvironment, CONFIG_LOCATION_PROPERTY) :
                MapUtil.getStr(sysProperties, CONFIG_LOCATION_PROPERTY, "");
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        configRetrieverOptions.addStore(getStoreOptions(configLocation, ""));
        if (StringUtil.isNotBlank(activeProfile)) {
            configRetrieverOptions.addStore(getStoreOptions(configLocation, DEFAULT_NAMES + "-" + activeProfile));
        }
        // 临时创建，后面读取到配置后会重建
        this.vertx = Vertx.vertx();
        System.out.println(BANNER);
        System.out.println(":::::::::::::: ".concat(ACTIVE_PROFILES_PROPERTY).concat(": ").concat(StringUtil.isBlank(activeProfile) ? "[default]" : activeProfile));
        System.out.println(":::::::::::::: ".concat(CONFIG_LOCATION_PROPERTY).concat(": ").concat(StringUtil.isBlank(configLocation) ? "[default]" : configLocation));
        ConfigRetriever retriever = ConfigRetriever.create(this.vertx, configRetrieverOptions);
        retriever.getConfig(this::prepare);
        return promise.future();
    }

    /**
     * prepare
     *
     * @param configResult 读取的配置信息结果(异步)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void prepare(AsyncResult<JsonObject> configResult) {
        if (configResult.failed()) {
            logger.error(configResult.cause().getLocalizedMessage());
            promise.fail(configResult.cause());
            stopwatch.stop();
        } else {
            JsonObject config = configResult.result();
            Map<VertxConfigurationProperties, Class<?>> propertiesClassMap = resolveConfigPropertyClasses();
            List<IocInstanceModule<?>> iocModules = new ArrayList<>(propertiesClassMap.size() + 1);
            if (!propertiesClassMap.isEmpty()) {
                // 映射配置类和实例,仅支持一层结构
                propertiesClassMap.forEach((k, v) -> {
                    try {
                        Object prop = config.getJsonObject(k.prefix()).mapTo(v);
                        iocModules.add(new IocInstanceModule(prop, v));
                    } catch (Exception e) {
                        logger.error("[vertx-boot] resolve config error: {}", e.getLocalizedMessage());
                    }
                });
            }
            // vertx 实例配置
            this.vertxProperties = config.getJsonObject("vertx").mapTo(VertxProperties.class);
            // 根据配置重新创建 vertx 实例
            this.vertx.close();
            this.vertx = Vertx.vertx(new VertxOptions()
                    .setEventLoopPoolSize(vertxProperties.getEventLoopPoolSize())
                    .setWorkerPoolSize(vertxProperties.getWorkerPoolSize())
                    .setBlockedThreadCheckInterval(4000)
                    .setMaxEventLoopExecuteTime(4000)
                    .setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS)
                    .setMaxWorkerExecuteTime(vertxProperties.getMaxWorkerExecuteTimeInSeconds())
                    .setMaxWorkerExecuteTimeUnit(TimeUnit.SECONDS));
            iocModules.add(new IocInstanceModule<>(vertxProperties, VertxProperties.class));
            installGuiceModules(iocModules.toArray(new IocInstanceModule[]{}));
            startedHandler(true, null);
        }
    }

    /**
     * 启动后回调
     *
     * @param succeeded
     * @param errMsg
     */
    private void startedHandler(boolean succeeded, String errMsg) {
        stopwatch.stop();
        if (succeeded) {
            if (logger.isInfoEnabled()) {
                logger.info("[vertx-boot] application has been ready in {} ms, you can deploy verticles now", stopwatch.elapsed().toMillis());
            }
        } else {
            logger.error("[vertx-boot] application start failed: {} ", errMsg);
        }
        // 是否启动 web server
        if (shouldStartWeb) {
            Integer port = this.vertxProperties.getServer().getPort();
            if (port != null) {
                logger.info("[vertx-boot] vertx default http server deploying...");
                this.vertx.deployVerticle(() -> new VertxHttpServerVerticle(injector, vertxProperties.getServer(), prepareRoutes()),
                        new DeploymentOptions().setInstances(vertxProperties.getServer().getInstances()));
            }
        } else {
            logger.info("[vertx-boot] will not start web server.");
        }
        // 增加hook undeploy 所有 verticles
        Runtime.getRuntime().addShutdownHook(new Thread(() -> vertx.deploymentIDs().forEach(id -> vertx.undeploy(id))));
        promise.complete(this);
    }

    /**
     * 解析 vertx 路由类 交给 {@link VertxHttpServerVerticle} 处理
     *
     * @return Collection
     */
    private Set<Class<?>> prepareRoutes() {
        VertxBootApplication annotation = this.primarySource.getAnnotation(VertxBootApplication.class);
        if (annotation == null) {
            logger.warn("annotation `VertxBootApplication` is not provided");
            return Collections.emptySet();
        }
        String[] basePackages = annotation.basePackages();
        if (basePackages.length == 0) {
            basePackages = new String[]{"org.winterfell", "cn.piesat"};
        }
        HashSet<Class<?>> set = new HashSet<>(16);
        for (String basePackage : basePackages) {
            set.addAll(ClassUtil.scanPackageByAnnotation(basePackage, VertxRoute.class));
        }
        return set;
    }


    /**
     * install guice modules 初始化 injector
     * TODO: 依赖注入
     *
     * @param instanceModules 绑定的实例
     */
    @SuppressWarnings("rawtypes")
    private void installGuiceModules(IocInstanceModule... instanceModules) {
        // 注册所有用户自定义的绑定
        List<AbstractModule> modules = new ArrayList<>(1);
        modules.addAll(resolveUserBoundModules());
        if (instanceModules != null) {
            modules.addAll(Arrays.asList(instanceModules));
        }
        injector = Guice.createInjector(modules);
    }

    /**
     * 解析配置类
     *
     * @return
     */
    private Map<VertxConfigurationProperties, Class<?>> resolveConfigPropertyClasses() {
        if (this.primarySource == null) {
            logger.warn("`primarySource` is null");
            return Collections.emptyMap();
        }
        VertxBootApplication annotation = this.primarySource.getAnnotation(VertxBootApplication.class);
        if (annotation == null) {
            logger.warn("annotation `VertxBootApplication` is not provided");
            return Collections.emptyMap();
        }
        Map<VertxConfigurationProperties, Class<?>> ret = new HashMap<>(annotation.enableProperties().length);
        for (Class<?> propertyClass : annotation.enableProperties()) {
            VertxConfigurationProperties anno = propertyClass.getAnnotation(VertxConfigurationProperties.class);
            if (anno == null) {
                logger.error(String.format("class of [%s] should be annotated with `VertxConfigurationProperties`", propertyClass.getName()));
                continue;
            }
            ret.put(anno, propertyClass);
        }
        return ret;
    }

    /**
     * 解析注解中配置的 modules {@link VertxBootApplication }
     * 获取应用自定义的 modules
     *
     * @return
     */
    private Collection<AbstractModule> resolveUserBoundModules() {
        if (this.primarySource == null) {
            return Collections.emptyList();
        }
        VertxBootApplication annotation = this.primarySource.getAnnotation(VertxBootApplication.class);
        if (annotation == null) {
            return Collections.emptyList();
        }
        String[] basePackages = annotation.basePackages();
        if (basePackages.length == 0) {
            basePackages = new String[]{"org.winterfell", "cn.piesat"};
        }
        Collection<AbstractModule> modules = new ArrayList<>(basePackages.length);
        Arrays.stream(basePackages).forEach(basePackage -> {
            Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation(basePackage, VertxComponent.class);
            // 绑定module
            if (!classSet.isEmpty()) {
                classSet.forEach(clazz -> {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (interfaces.length > 0) {
                        for (Class<?> anInterface : interfaces) {
                            if (!anInterface.getPackage().getName().startsWith("java")) {
                                modules.add(new IocInterfaceModule(clazz, anInterface));
                            }
                        }
                    } else {
                        modules.add(new IocClassModule(clazz));
                    }
                });
            }
        });
        return modules;
//        return Arrays.stream(annotation.injectModules()).map(ReflectionUtils::newInstance).collect(Collectors.toList());
    }

    /**
     * create vertx instance with options or not
     *
     * @param options vertx 选项
     * @return
     */
    private Vertx createVertx(VertxOptions options) {
        return Objects.isNull(options) ? Vertx.vertx() : Vertx.vertx(options);
    }

    /**
     * 定义配置存储参数
     *
     * @param cfgPath 配置位置
     * @param cfgName 名字匹配
     * @return
     */
    private ConfigStoreOptions getStoreOptions(String cfgPath, String cfgName) {
        String name = StringUtil.isBlank(cfgName) ? DEFAULT_NAMES : cfgName;
        String path = StringUtil.isBlank(cfgPath) ? "" : cfgPath.endsWith(File.separator) ? cfgPath : cfgPath.concat(File.separator);
        return new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject()
                        .put("path", path.concat(name).concat(".yml")));
    }

    /**
     * 环境变量,包含系统环境变量
     * eg: `export PATH=/home/file/to`
     *
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map<String, Object> getSystemEnvironment() {
        try {
            return (Map) System.getenv();
        } catch (AccessControlException ex) {
            return (Map) new ReadOnlySystemAttributesMap() {
                @Override
                @Nullable
                protected String getSystemAttribute(String attributeName) {
                    try {
                        return System.getenv(attributeName);
                    } catch (AccessControlException ex) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Caught AccessControlException when accessing system environment variable '" +
                                    attributeName + "'; its value will be returned [null]. Reason: " + ex.getMessage());
                        }
                        return null;
                    }
                }
            };
        }
    }

    /**
     * jvm 系统参数
     * eg: -Dprofile.active=dev
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSystemProperties() {
        try {
            return (Map) System.getProperties();
        } catch (AccessControlException ex) {
            return (Map) new ReadOnlySystemAttributesMap() {
                @Override
                protected String getSystemAttribute(String attributeName) {
                    try {
                        return System.getProperty(attributeName);
                    } catch (AccessControlException ex) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Caught AccessControlException when accessing system property '" +
                                    attributeName + "'; its value will be returned [null]. Reason: " + ex.getMessage());
                        }
                        return null;
                    }
                }
            };
        }
    }
}
