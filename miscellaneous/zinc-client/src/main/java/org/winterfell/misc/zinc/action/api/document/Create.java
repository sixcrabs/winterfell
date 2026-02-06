package org.winterfell.misc.zinc.action.api.document;


import org.winterfell.misc.zinc.action.AbstractDocumentTargetedAction;
import org.winterfell.misc.zinc.action.BulkableZincAction;
import org.winterfell.misc.zinc.http.HttpRequestMethod;

/**
 * <p>
 * .https://docs.zincsearch.com/api/document/create/
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class Create extends AbstractDocumentTargetedAction<DocumentResult> implements BulkableZincAction<DocumentResult> {

    public Create(Builder builder) {
        super(builder);
        this.payload = builder.source;
    }

    /**
     * request method
     *
     * @return
     */
    @Override
    public HttpRequestMethod getRequestMethod() {
        return (id != null) ? HttpRequestMethod.PUT : HttpRequestMethod.POST;
    }

    /**
     * eg 'index' 'update' 'delete'
     *
     * @return
     */
    @Override
    public String getBulkMethodName() {
        return "index";
    }

    public static class Builder extends AbstractDocumentTargetedAction.Builder<Create, Builder> {

        private final Object source;

        public Builder(Object source) {
            this.source = source;
//            this.id(getIdFromSource(source)); // TODO: set the default for id if it exists in source
        }

        @Override
        public Create build() {
            return new Create(this);
        }
    }


}
