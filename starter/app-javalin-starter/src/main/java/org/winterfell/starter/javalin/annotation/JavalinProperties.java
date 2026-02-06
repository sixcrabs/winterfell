package org.winterfell.starter.javalin.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * .定义为一个需要解析并注入到app的配置项
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/26
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JavalinProperties {

    /**
     * 配置项 前缀
     * @return
     */
    String prefix();
}
