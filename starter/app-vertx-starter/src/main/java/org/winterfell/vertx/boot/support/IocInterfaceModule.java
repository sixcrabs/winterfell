package org.winterfell.vertx.boot.support;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * <p>
 * .guice 绑定接口和实现 默认单例
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/27
 */
public class IocInterfaceModule extends AbstractModule {

    private final Class interfaceClass;

    public IocInterfaceModule(Class implClass, Class interfaceClass) {
        this.implClass = implClass;
        this.interfaceClass = interfaceClass;
    }

    private final Class implClass;


    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(interfaceClass).to(implClass).in(Scopes.SINGLETON);
    }
}
