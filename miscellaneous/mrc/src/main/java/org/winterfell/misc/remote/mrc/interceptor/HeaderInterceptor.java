package org.winterfell.misc.remote.mrc.interceptor;

import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author alex
 * @version v1.0 2020/4/14
 */
public enum  HeaderInterceptor implements Interceptor {

    /**
     * singleton
     */
    INSTANCE;

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request().newBuilder().addHeader("X-Micro-Rest-Client", "true").build());
    }
}
