package org.winterfell.misc.zinc.action.api;


import org.winterfell.misc.zinc.action.AbstractZincAction;
import org.winterfell.misc.zinc.http.HttpRequestMethod;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class GetMapping extends AbstractZincAction<GetMappingResult> {

    private GetMapping(Builder builder) {
        super(builder);
    }

    /**
     * 子类可以实现该方法修改请求url
     *
     * @return
     */
    @Override
    protected String buildURI() {
        return "/api/" + indexName + "/_mapping";
    }

    /**
     * request method
     *
     * @return
     */
    @Override
    public HttpRequestMethod getRequestMethod() {
        return HttpRequestMethod.GET;
    }

    public static class Builder extends AbstractZincAction.Builder<GetMapping, Builder> {

        public Builder() {
            setHeader("accept", "application/json");
            setHeader("content-type", "application/json");
        }

        @Override
        public GetMapping build() {
            return new GetMapping(this);
        }
    }
}
