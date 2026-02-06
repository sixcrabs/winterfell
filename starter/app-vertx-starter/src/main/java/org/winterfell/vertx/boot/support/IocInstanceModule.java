package org.winterfell.vertx.boot.support;

import com.google.inject.AbstractModule;

/**
 * <p>
 * guice module 绑定实例
 * </p>
 *
 * @author alex
 * @version v1.0
 */
public class IocInstanceModule<T> extends AbstractModule {

    private final T instance;

    private final Class<T> clazz;

    public IocInstanceModule(T instance, Class<T> clazz) {
        this.instance = instance;
        this.clazz = clazz;
    }

    @Override
    protected void configure() {
        // 默认单例
        bind(clazz).toInstance(instance);
    }
}
