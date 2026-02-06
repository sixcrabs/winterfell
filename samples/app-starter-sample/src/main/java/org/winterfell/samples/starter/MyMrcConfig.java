package org.winterfell.samples.starter;

import org.winterfell.misc.remote.mrc.autoconfigure.MrClientConfigBuilderCustomizer;
import org.winterfell.misc.remote.mrc.autoconfigure.MrClientInterceptorConfigurer;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/28
 */
@Configuration
public class MyMrcConfig implements MrClientInterceptorConfigurer, MrClientConfigBuilderCustomizer {

    /**
     * defined interceptor
     *
     * @return
     */
    @Override
    public Interceptor config() {
        return new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                return chain.proceed(chain.request().newBuilder().addHeader("X-all-name", "Snow").build());
            }
        };
    }

    /**
     * 定制 http client 的 build 参数
     *
     * @param builder {@link OkHttpClient.Builder}
     */
    @Override
    public void customize(OkHttpClient.Builder builder) {
        
    }
}
