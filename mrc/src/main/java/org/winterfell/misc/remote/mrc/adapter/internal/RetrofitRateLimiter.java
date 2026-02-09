package org.winterfell.misc.remote.mrc.adapter.internal;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
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
public interface RetrofitRateLimiter {

    /**
     * Decorate {@link Call}s allow {@link CircuitBreaker} functionality.
     *
     * @param rateLimiter {@link RateLimiter} to apply
     * @param call        Call to decorate
     * @param <T>         Response type of call
     * @return Original Call decorated with CircuitBreaker
     */
    static <T> Call<T> decorateCall(final RateLimiter rateLimiter, final Call<T> call) {
        return new RetrofitRateLimiter.RateLimitingCall<>(call, rateLimiter);
    }

    class RateLimitingCall<T> extends DecoratedCall<T> {

        private final Call<T> call;
        private final RateLimiter rateLimiter;

        public RateLimitingCall(Call<T> call, RateLimiter rateLimiter) {
            super(call);
            this.call = call;
            this.rateLimiter = rateLimiter;
        }

        @Override
        public void enqueue(final Callback<T> callback) {
            try {
                RateLimiter.waitForPermission(rateLimiter);
            } catch (RequestNotPermitted | IllegalStateException e) {
                callback.onResponse(call, tooManyRequestsError());
                return;
            }

            call.enqueue(callback);
        }

        @Override
        public Response<T> execute() throws IOException {
            CheckedFunction0<Response<T>> restrictedSupplier = RateLimiter
                    .decorateCheckedSupplier(rateLimiter, call::execute);
            final Try<Response<T>> response = Try.of(restrictedSupplier);
            return response.isSuccess() ? response.get() : handleFailure(response);
        }

        private Response<T> handleFailure(Try<Response<T>> response) throws IOException {
            try {
                throw response.getCause();
            } catch (RequestNotPermitted | IllegalStateException e) {
                return tooManyRequestsError();
            } catch (IOException ioe) {
                throw ioe;
            } catch (Throwable t) {
                throw new RuntimeException("Exception executing call", t);
            }
        }

        private Response<T> tooManyRequestsError() {
            return Response.error(429, ResponseBody
                    .create(MediaType.parse("text/plain"), "Too many requests for the client"));
        }

        @Override
        public Call<T> clone() {
            return new RetrofitRateLimiter.RateLimitingCall<>(call.clone(), rateLimiter);
        }
    }
}
