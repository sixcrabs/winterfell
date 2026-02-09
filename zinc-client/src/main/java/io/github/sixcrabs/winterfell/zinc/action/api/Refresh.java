package io.github.sixcrabs.winterfell.zinc.action.api;


import io.github.sixcrabs.winterfell.zinc.action.AbstractZincAction;
import io.github.sixcrabs.winterfell.zinc.action.GenericResultAbstractZincAction;
import io.github.sixcrabs.winterfell.zinc.http.HttpRequestMethod;

/**
 * <p>
 * .https://docs.zincsearch.com/api/index/refresh-data/
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class Refresh extends GenericResultAbstractZincAction {

    protected Refresh(AbstractZincAction.Builder builder) {
        super(builder);
    }

    /**
     * request method
     *
     * @return
     */
    @Override
    public HttpRequestMethod getRequestMethod() {
        return HttpRequestMethod.POST;
    }

    /**
     * 子类可以实现该方法修改请求url
     *
     * @return
     */
    @Override
    protected String buildURI() {
        return "/api/index/" + indexName + "/refresh";
    }

    public static class Builder extends AbstractZincAction.Builder<Refresh, Builder> {
        @Override
        public Refresh build() {
            return new Refresh(this);
        }
    }
}