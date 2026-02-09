package io.github.sixcrabs.winterfell.zinc.action.api.document;

import io.github.sixcrabs.winterfell.zinc.action.AbstractDocumentTargetedAction;
import io.github.sixcrabs.winterfell.zinc.http.HttpRequestMethod;

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