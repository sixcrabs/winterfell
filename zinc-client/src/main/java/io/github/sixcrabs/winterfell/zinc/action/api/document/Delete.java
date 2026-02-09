package io.github.sixcrabs.winterfell.zinc.action.api.document;


import io.github.sixcrabs.winterfell.zinc.action.AbstractDocumentTargetedAction;
import io.github.sixcrabs.winterfell.zinc.action.BulkableZincAction;
import io.github.sixcrabs.winterfell.zinc.http.HttpRequestMethod;

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