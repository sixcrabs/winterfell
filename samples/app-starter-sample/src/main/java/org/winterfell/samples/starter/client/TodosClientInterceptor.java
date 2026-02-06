package org.winterfell.samples.starter.client;

import org.winterfell.misc.remote.mrc.interceptor.MrcInterceptor;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/13
 */
public class TodosClientInterceptor implements MrcInterceptor {
    @Override
    public boolean shouldSkip() {
        return false;
    }

    @Override
    public Interceptor nativeInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                System.out.println("我是TodoClient 的拦截器！");
                return chain.proceed(chain.request());
            }
        };
    }
}
