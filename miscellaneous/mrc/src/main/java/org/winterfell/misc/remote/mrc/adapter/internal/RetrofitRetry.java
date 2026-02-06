package org.winterfell.misc.remote.mrc.adapter.internal;

import io.github.resilience4j.retry.Retry;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/19
 */
public interface RetrofitRetry {

    /**
     * Decorate {@link Call}s allow Retry functionality.
     *
     * @param retry Retry to apply
     * @param call  Call to decorate
     * @return Original Call decorated with Retry
     */
    static <T> Call<T> decorateCall(final Retry retry, final Call<T> call) {
        return new RetrofitRetry.RetryCall<>(call, retry);
    }

    class RetryCall<T> extends DecoratedCall<T> {

        private final Call<T> call;
        private final Retry retry;
        private final Retry.Context<Response<T>> context;

        public RetryCall(Call<T> call, Retry retry) {
            super(call);
            this.call = call;
            this.retry = retry;
            this.context = retry.context();
        }

        private Callback<T> retriedCallback(Callback<T> callback) {
            return new Callback<T>() {

                /**
                 * Invoked for a received HTTP response.
                 */
                @Override
                public void onResponse(Call<T> call, Response<T> response) {
                    if (context.onResult(response)) {
                        executableCall().enqueue(retriedCallback(callback));
                    } else {
                        context.onComplete();
                        callback.onResponse(call, response);
                    }
                }

                /**
                 * Invoked when a network exception occurred talking to the server or when an unexpected
                 * exception occurred creating the request or processing the response.
                 */
                @Override
                public void onFailure(Call<T> call, Throwable throwable) {
                    try {
                        context.onError(asException(throwable));
                        executableCall().enqueue(retriedCallback(callback));
                    } catch (Exception exception) {
                        callback.onFailure(call, exception);
                    }
                }

                /**
                 * resilience4j accepts exceptions only
                 */
                private Exception asException(Throwable throwable) {
                    return throwable instanceof Exception ?
                            (Exception) throwable :
                            new RuntimeException("Throwable", throwable);
                }
            };
        }

        /**
         * Asynchronously send the request and notify callback of its response or if an error
         * occurred talking to the server, creating the request, or processing the response.
         */
        @Override
        public void enqueue(final Callback<T> callback) {
            call.enqueue(retriedCallback(callback));
        }

        /**
         * Synchronously send the request and return its response
         */
        @Override
        public Response<T> execute() throws IOException {
            Response<T> response = null;

            try {
                response = retry.executeCallable(() -> executableCall().execute());
            } catch (IOException ioe) {
                throw ioe;
            } catch (Throwable t) {
                throw new RuntimeException("Exception executing call", t);
            }
            return response;
        }

        /**
         * Returns cloned call, if the call has been either {@linkplain #execute() executed} or {@linkplain
         * #enqueue(Callback) enqueued} already else would return the actual call.
         */
        private Call<T> executableCall() {
            if (call.isExecuted())
                return call.clone();
            return call;
        }

        /**
         * Create a new, identical call to this one which can be enqueued or executed even if this call
         * has already been.
         */
        @Override
        public Call<T> clone() {
            return (new RetrofitRetry.RetryCall<>(executableCall(), retry));
        }

        /**
         * Returns true if this call has been either {@linkplain #execute() executed} or {@linkplain
         * #enqueue(Callback) enqueued}. It is an error to execute or enqueue a call more than once.
         */
        @Override
        public boolean isExecuted() {
            return call.isExecuted();
        }

        /**
         * True if {@link #cancel()} was called.
         */
        @Override
        public boolean isCanceled() {
            return call.isCanceled();
        }

        /**
         * Cancel this call. An attempt will be made to cancel in-flight calls, and if the call has not
         * yet been executed it never will be.
         */
        @Override
        public void cancel() {
            call.cancel();
        }

        /**
         * The original HTTP request.
         */
        @Override
        public Request request() {
            return call.request();
        }
    }
}
