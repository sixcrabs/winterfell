package org.winterfell.misc.zinc.action.api.document;


import org.winterfell.misc.zinc.action.AbstractDocumentTargetedAction;
import org.winterfell.misc.zinc.action.BulkableZincAction;
import org.winterfell.misc.zinc.http.HttpRequestMethod;

/**
 * <p>
 * https://docs.zincsearch.com/api/document/update/
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class Update extends AbstractDocumentTargetedAction<DocumentResult> implements BulkableZincAction<DocumentResult> {

    public Update(Builder builder) {
        super(builder);
        this.payload = builder.payload;
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
     * eg 'index' 'update' 'delete'
     *
     * @return
     */
    @Override
    public String getBulkMethodName() {
        return "update";
    }


    @Override
    protected String buildURI() {
        return "/api/" + indexName + "/_update/" + id;
    }

    public static class Builder extends AbstractDocumentTargetedAction.Builder<Update, Builder> {

        private final Object payload;

        public Builder(Object payload) {
            this.payload = payload;
        }

        @Override
        public Update build() {
            return new Update(this);
        }
    }
}
