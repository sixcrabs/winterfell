package org.winterfell.vertx.boot.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * 标记一个 properties 类
 * </p>
 *
 * @author alex
 * @version v1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VertxConfigurationProperties {

    /**
     * 配置项前缀
     * 注： 目前仅支持单个值
     *
     * @return
     */
    String prefix();
}
