package org.winterfell.misc.remote.mrc.interceptor;

import okhttp3.Interceptor;

/**
 * <p>
 * 每个client注解指定的拦截器
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/13
 */
public interface MrcInterceptor {


    /**
     * 是否跳过
     * @return
     */
    boolean shouldSkip();


    /**
     * okhttp 里原生的拦截器
     * @return
     */
    Interceptor nativeInterceptor();


}
