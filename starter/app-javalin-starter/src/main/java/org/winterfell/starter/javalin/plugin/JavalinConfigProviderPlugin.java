package org.winterfell.starter.javalin.plugin;

import org.winterfell.starter.javalin.JavalinAppConfig;
import org.winterfell.starter.javalin.support.JavalinPropertiesResolver;
import io.javalin.Javalin;
import io.javalin.core.plugin.Plugin;
import io.javalin.core.plugin.PluginLifecycleInit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  javalin 插件
 * <p>
 * javalin config provider plugin
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/26
 */
public class JavalinConfigProviderPlugin implements PluginLifecycleInit, Plugin {

    private static final Logger LOG = LoggerFactory.getLogger(JavalinConfigProviderPlugin.class);

    /**
     * 获取到配置信息 进行数据库ORM的初始化
     *
     * @param app
     */
    @Override
    public void init(@NotNull Javalin app) {
        app.attribute(JavalinAppConfig.class.getName(), JavalinPropertiesResolver.INSTANCE.getAppConfig());
        // 可通过全路径访问某个配置项 eg app.attribute("app.xx.yy")
        JavalinPropertiesResolver.INSTANCE.getFlattenedMap().forEach(app::attribute);
        // bean 对象
        JavalinPropertiesResolver.INSTANCE.getBeanProperties().forEach(app::attribute);
    }

    @Override
    public void apply(@NotNull Javalin app) {
        System.out.println("-------apply-----");

    }


}
