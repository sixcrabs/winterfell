package org.winterfell.misc.remote.mrc.adapter;

import org.winterfell.misc.remote.mrc.support.MrClientException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * @author alex
 * @version v1.0 2020/4/9
 */
public class ExecuteCallAdapter extends CallAdapter.Factory {
    /**
     * Returns a call adapter for interface methods that return {@code returnType}, or null if it
     * cannot be handled by this factory.
     *
     * @param returnType
     * @param annotations
     * @param retrofit
     */
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) == Call.class) {
            // 如果定义方法返回就是 Call 交由 default 去处理
            return null;
        }
        final Type responseType = getResponseType(returnType);
        return new CallAdapter<Object, Object>() {
            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public Object adapt(Call<Object> call) {
                try {
                    Response<Object> response = call.execute();
                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        if (Objects.nonNull(response.errorBody())) {
                            try (ResponseBody error = response.errorBody()) {
                                String msg = String.format("code: %d, message: %s error: %s", response.code(), response.message(), error);
                                throw new MrClientException(msg);
                            }

                        }
                    }
                } catch (CallNotPermittedException e) {
                    throw e;
                } catch (Exception e) {
                    throw new MrClientException(e);
                }
                return null;
            }
        };
    }

    private Type getResponseType(Type type) {
        if (type instanceof WildcardType) {
            return ((WildcardType) type).getUpperBounds()[0];
        }
        return type;
    }
}
