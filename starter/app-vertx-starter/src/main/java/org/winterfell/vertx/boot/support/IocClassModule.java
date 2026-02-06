package org.winterfell.vertx.boot.support;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * <p>
 * .guice 绑定当前类为单例
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/27
 */
public class IocClassModule<T> extends AbstractModule {

    public IocClassModule(Class<T> clazz) {
        this.clazz = clazz;
    }

    private final Class<T> clazz;

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(clazz).in(Scopes.SINGLETON);
    }
}
