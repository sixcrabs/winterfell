package org.winterfell.misc.remote.mrc.autoconfigure;

import okhttp3.Interceptor;

/**
 * <p>
 * 配置拦截器
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/4/10
 */
@FunctionalInterface
public interface MrClientInterceptorConfigurer {

    /**
     * defined interceptor
     * @return
     */
    Interceptor config();

}
