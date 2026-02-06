package org.winterfell.misc.remote.mrc.adapter.internal;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

/**
 * Simple decorator class that implements Call&lt;T&gt; and delegates all calls the the Call
 * instance provided in the constructor.  Methods can be overridden as required.
 *
 * @param <T> Call parameter type
 *
 * @author Alex
 * @version v1.0 2024/11/19
 */
public abstract class DecoratedCall<T> implements Call<T> {

    private final Call<T> call;

    public DecoratedCall(Call<T> call) {
        this.call = call;
    }

    @Override
    public Response<T> execute() throws IOException {
        return this.call.execute();
    }

    @Override
    public void enqueue(Callback<T> callback) {
        call.enqueue(callback);
    }

    @Override
    public boolean isExecuted() {
        return call.isExecuted();
    }

    @Override
    public void cancel() {
        call.cancel();
    }

    @Override
    public boolean isCanceled() {
        return call.isCanceled();
    }

    @Override
    public abstract Call<T> clone();

    @Override
    public Request request() {
        return call.request();
    }
}