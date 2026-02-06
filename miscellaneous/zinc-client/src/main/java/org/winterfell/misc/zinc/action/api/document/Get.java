package org.winterfell.misc.zinc.action.api.document;

import org.winterfell.misc.zinc.action.AbstractDocumentTargetedAction;
import org.winterfell.misc.zinc.http.HttpRequestMethod;

/**
 * <p>
 *  GET `http://localhost:4080/api/{index}/_doc/{id}`
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/4/24
 */
public class Get extends AbstractDocumentTargetedAction<DocumentResult> {

    public Get(Builder builder) {
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

    public static class Builder extends AbstractDocumentTargetedAction.Builder<Get, Get.Builder> {

        public Builder(String id) {
            this.id(id);
        }

        @Override
        public Get build() {
            return new Get(this);
        }
    }
}
