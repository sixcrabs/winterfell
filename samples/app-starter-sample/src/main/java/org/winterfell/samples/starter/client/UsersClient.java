package org.winterfell.samples.starter.client;

import org.winterfell.misc.remote.mrc.MrClient;
import org.winterfell.misc.remote.mrc.support.MrClientCircuitBreakerParam;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/13
 */

@MrClient(name = "users-client", url = "https://jsonplaceholder.typicode.com/",
        circuitBreaker = @MrClientCircuitBreakerParam(enabled = true, failureRateThreshold=60, permittedNumberOfCallsInHalfOpenState = 5, waitDurationInOpenStateSeconds = 10))
public interface UsersClient {

    @GET("users/{id}")
    Map getUser(@Path("id") String id);
}
