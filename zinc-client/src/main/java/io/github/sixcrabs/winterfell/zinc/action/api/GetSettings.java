package io.github.sixcrabs.winterfell.zinc.action.api;


import io.github.sixcrabs.winterfell.zinc.action.AbstractZincAction;
import io.github.sixcrabs.winterfell.zinc.action.GenericResultAbstractZincAction;
import io.github.sixcrabs.winterfell.zinc.http.HttpRequestMethod;

/**
 * <p>
 * get settings
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class GetSettings extends GenericResultAbstractZincAction {

    public GetSettings() {
    }

    public GetSettings(Builder builder) {
        super(builder);
    }

    /**
     * 子类可以实现该方法修改请求url
     *
     * @return
     */
    @Override
    protected String buildURI() {
        return "/es/" + indexName + "/_settings";
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

    public static class Builder extends AbstractZincAction.Builder<GetSettings, Builder> {


        @Override
        public GetSettings build() {
            return new GetSettings(this);
        }
    }
}