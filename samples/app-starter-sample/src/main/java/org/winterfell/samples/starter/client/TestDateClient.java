package org.winterfell.samples.starter.client;

import org.winterfell.misc.remote.mrc.MrClient;
import org.winterfell.samples.starter.client.domain.MrcTestDTO;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/1/20
 */
@MrClient(name = "test-date-client", url = "http://127.0.0.1:9309")
public interface TestDateClient {


    @GET("/api/person/{id}")
    List<MrcTestDTO> getDto(@Path("id") String id);
}
