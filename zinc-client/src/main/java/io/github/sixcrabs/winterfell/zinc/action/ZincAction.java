package io.github.sixcrabs.winterfell.zinc.action;

import io.github.sixcrabs.winterfell.zinc.ZincResult;
import io.github.sixcrabs.winterfell.zinc.http.HttpRequestMethod;
import com.google.gson.Gson;

import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
public interface ZincAction<T extends ZincResult> {

    /**
     * request method
     * @return
     */
    HttpRequestMethod getRequestMethod();

    /**
     * request uri
     * @return
     */
    String getRequestURI();

    /**
     * get data
     * @param gson
     * @return
     */
    String getData(Gson gson);

    /**
     * get json path of result
     * @return
     */
    String getPathToResult();

    /**
     * get headers
     * @return
     */
    Map<String, Object> getHeaders();

    /**
     *
     * @param responseBody
     * @param statusCode
     * @param reasonPhrase
     * @param gson
     * @return
     */
    T createNewResult(String responseBody, int statusCode, String reasonPhrase, Gson gson);
}