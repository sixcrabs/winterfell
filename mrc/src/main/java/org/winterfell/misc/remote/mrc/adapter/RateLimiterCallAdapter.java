package org.winterfell.misc.remote.mrc.adapter;

import org.winterfell.misc.remote.mrc.adapter.internal.RetrofitRateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiter;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/19
 */
public final class RateLimiterCallAdapter extends CallAdapter.Factory {

    private final RateLimiter rateLimiter;

    private RateLimiterCallAdapter(final RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    /**
     * Create a rate-limiting call adapter factory that decorates retrofit calls
     *
     * @param rateLimiter rate limiter to use
     * @return a {@link CallAdapter.Factory} that can be passed into the {@link Retrofit.Builder}
     */
    public static RateLimiterCallAdapter of(final RateLimiter rateLimiter) {
        return new RateLimiterCallAdapter(rateLimiter);
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        @SuppressWarnings("unchecked")
        CallAdapter<Object, Object> nextAdapter = (CallAdapter<Object, Object>) retrofit
                .nextCallAdapter(this, returnType, annotations);

        return new CallAdapter<Object, Object>() {
            @Override
            public Type responseType() {
                return nextAdapter.responseType();
            }

            @Override
            public Object adapt(Call<Object> call) {
                return nextAdapter.adapt(RetrofitRateLimiter.decorateCall(rateLimiter, call));
            }
        };
    }
}
