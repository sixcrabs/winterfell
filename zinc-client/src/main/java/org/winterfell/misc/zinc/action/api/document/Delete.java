package org.winterfell.misc.zinc.action.api.document;


import org.winterfell.misc.zinc.action.AbstractDocumentTargetedAction;
import org.winterfell.misc.zinc.action.BulkableZincAction;
import org.winterfell.misc.zinc.http.HttpRequestMethod;

/**
 * <p>
 * .https://docs.zincsearch.com/api/document/delete/
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class Delete extends AbstractDocumentTargetedAction<DocumentResult> implements BulkableZincAction<DocumentResult> {

    public Delete(Builder builder) {
        super(builder);
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

    /**
     * eg 'index' 'update' 'delete'
     *
     * @return
     */
    @Override
    public String getBulkMethodName() {
        return "delete";
    }

    public static class Builder extends AbstractDocumentTargetedAction.Builder<Delete, Builder> {

        public Builder(String id) {
            this.id(id);
        }

        @Override
        public Delete build() {
            return new Delete(this);
        }
    }
}
