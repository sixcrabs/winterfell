package org.winterfell.misc.remote.mrc.interceptor;

import com.google.gson.Gson;
import okhttp3.Interceptor;
import okhttp3.Response;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

import static org.winterfell.misc.remote.mrc.support.MrConstants.HTTP_METHOD_GET;
import static org.winterfell.misc.remote.mrc.support.MrConstants.HTTP_METHOD_POST;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/4/14
 */
public enum LogInterceptor implements Interceptor {

    /**
     * singleton
     */
    INSTANCE;

    private static final Gson GSON = new Gson();

    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        String method = chain.request().method();
        if (HTTP_METHOD_GET.equalsIgnoreCase(method)) {
            log.info("[mr-client] curl {}", chain.request().url());
        } else if (HTTP_METHOD_POST.equalsIgnoreCase(method)) {
            try {
                Buffer buffer = new Buffer();
                Objects.requireNonNull(chain.request().body()).writeTo(buffer);
                log.info("[mr-client] curl {} -X POST  -d '{}' --header \"Content-Type: application/json\"", chain.request().url(), buffer);
            } catch (IOException e) {
                log.warn(e.getLocalizedMessage());
            }
        } else {
            log.info("[mr-client] request url: {}", chain.request().url());
        }
        return chain.proceed(chain.request());
    }
    }
