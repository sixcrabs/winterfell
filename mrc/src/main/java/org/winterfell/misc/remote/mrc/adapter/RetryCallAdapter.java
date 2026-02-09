package org.winterfell.misc.remote.mrc.adapter;

import org.winterfell.misc.remote.mrc.adapter.internal.RetrofitRetry;
import io.github.resilience4j.retry.Retry;
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
public final class RetryCallAdapter extends CallAdapter.Factory {

    private final Retry retry;

    private RetryCallAdapter(final Retry retry) {
        this.retry = retry;
    }

    /**
     * Create a retry call adapter that decorates retrofit calls
     *
     * @return a {@link CallAdapter.Factory} that can be passed into the {@link Retrofit.Builder}
     */
    public static RetryCallAdapter of(Retry retry) {
        return new RetryCallAdapter(retry);
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        @SuppressWarnings("unchecked")
        CallAdapter<Object, Object> nextAdapter =
                (CallAdapter<Object, Object>) retrofit.nextCallAdapter(this, returnType, annotations);

        return new CallAdapter<Object, Object>() {
            @Override
            public Type responseType() {
                return nextAdapter.responseType();
            }

            @Override
            public Object adapt(Call<Object> call) {
                return nextAdapter.adapt(RetrofitRetry.decorateCall(retry, call));
            }
        };
    }
}