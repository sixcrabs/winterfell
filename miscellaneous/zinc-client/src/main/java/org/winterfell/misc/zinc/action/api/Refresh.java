package org.winterfell.misc.zinc.action.api;


import org.winterfell.misc.zinc.action.AbstractZincAction;
import org.winterfell.misc.zinc.action.GenericResultAbstractZincAction;
import org.winterfell.misc.zinc.http.HttpRequestMethod;

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
