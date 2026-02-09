package org.winterfell.misc.zinc.action.api;


import org.winterfell.misc.zinc.action.AbstractZincAction;
import org.winterfell.misc.zinc.action.GenericResultAbstractZincAction;
import org.winterfell.misc.zinc.http.HttpRequestMethod;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class IndexList extends GenericResultAbstractZincAction {

    protected IndexList(Builder builder) {
        super(builder);
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


    /**
     * 子类可以实现该方法修改请求url
     *
     * @return
     */
    @Override
    protected String buildURI() {
        return super.buildURI() + "/api/index";
    }

    public static class Builder extends AbstractZincAction.Builder<IndexList, Builder> {

        public Builder pageNum(int pageNum) {
            this.setParameter("page_num", pageNum);
            return this;
        }

        public Builder pageSize(int pageSize) {
            this.setParameter("page_size", pageSize);
            return this;
        }

        public Builder sortBy(String sortFieldName) {
            this.setParameter("sort_by", sortFieldName);
            return this;
        }

        public Builder nameQuery(String name) {
            this.setParameter("name", name);
            return this;
        }

        @Override
        public IndexList build() {
            return new IndexList(this);
        }
    }
}
