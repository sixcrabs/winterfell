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
public class IndexDelete extends AbstractZincAction<IndexResult> {

    public IndexDelete() {
    }

    public IndexDelete(AbstractZincAction.Builder builder) {
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
        return HttpRequestMethod.DELETE;
    }

    public static class Builder extends AbstractZincAction.Builder<IndexDelete, Builder> {

        @Override
        public IndexDelete build() {
            return new IndexDelete(this);
        }
    }
}
