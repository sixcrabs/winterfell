package io.github.sixcrabs.winterfell.zinc.action.es;

import io.github.sixcrabs.winterfell.zinc.http.HttpRequestMethod;
import com.google.gson.Gson;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
public class EsInfo extends AbstractEsZincAction<EsInfoResult> {
    /**
     * request method
     *
     * @return
     */
    @Override
    public HttpRequestMethod getRequestMethod() {
        return HttpRequestMethod.GET;
    }

    @Override
    public EsInfoResult createNewResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewResult(new EsInfoResult(gson), responseBody, statusCode, reasonPhrase, gson);
    }

}