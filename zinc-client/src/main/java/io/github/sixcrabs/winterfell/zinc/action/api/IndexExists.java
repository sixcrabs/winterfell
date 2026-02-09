package io.github.sixcrabs.winterfell.zinc.action.api;


import io.github.sixcrabs.winterfell.zinc.action.AbstractZincAction;
import io.github.sixcrabs.winterfell.zinc.action.GenericResultAbstractZincAction;
import io.github.sixcrabs.winterfell.zinc.http.HttpRequestMethod;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class IndexExists extends GenericResultAbstractZincAction {

    public IndexExists() {
    }

    public IndexExists(Builder builder) {
        super(builder);
    }

    /**
     * 子类可以实现该方法修改请求url
     *
     * @return
     */
    @Override
    protected String buildURI() {
        return "/api/index/" + super.buildURI();
    }

    /**
     * request method
     *
     * @return
     */
    @Override
    public HttpRequestMethod getRequestMethod() {
        return HttpRequestMethod.HEAD;
    }

    public static class Builder extends AbstractZincAction.Builder<IndexExists, Builder> {

        @Override
        public IndexExists build() {
            return new IndexExists(this);
        }
    }
}