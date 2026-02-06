package org.winterfell.samples.starter.client;

import org.winterfell.misc.remote.mrc.MrClient;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.Map;

/**
 * @author alex
 * @version v1.0 2021/7/25
 */
@MrClient(name = "todo-client", url = "https://jsonplaceholder.typicode.com/",
        interceptor = TodosClientInterceptor.class)
public interface TodosClient {

    @GET("todos/{id}")
     Map todos(@Path("id") String id);
}
