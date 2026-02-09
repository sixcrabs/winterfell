package org.winterfell.misc.zinc.action.api;

import org.winterfell.misc.zinc.action.AbstractZincAction;
import org.winterfell.misc.zinc.http.HttpRequestMethod;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class UserList extends AbstractZincAction<UserListResult> {
    /**
     * request method
     *
     * @return
     */
    @Override
    public HttpRequestMethod getRequestMethod() {
        return HttpRequestMethod.GET;
    }

    /**
     * 子类可以实现该方法修改请求url
     *
     * @return
     */
    @Override
    protected String buildURI() {
        return "/api/user";
    }

    /**
     * 解析成 json array
     *
     * @param result
     * @param responseBody
     * @param statusCode
     * @param reasonPhrase
     * @param gson
     * @return
     */
    @Override
    public UserListResult createNewResult(UserListResult result, String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        if (isHttpSuccessful(statusCode)) {
            JsonArray jsonArray = gson.fromJson(responseBody, JsonArray.class);
            result.fromJsonArray(jsonArray);
        } else {
            result.setErrorMessage(responseBody).setResponseCode(statusCode).setSucceeded(false).setJsonString(responseBody);
        }
        return result;
    }
}
