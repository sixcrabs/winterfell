package io.github.sixcrabs.winterfell.zinc.action.api;

import io.github.sixcrabs.winterfell.zinc.action.AbstractZincAction;
import io.github.sixcrabs.winterfell.zinc.action.GenericResultAbstractZincAction;
import io.github.sixcrabs.winterfell.zinc.http.HttpRequestMethod;
import io.github.sixcrabs.winterfell.zinc.model.DocumentDateTimeField;
import io.github.sixcrabs.winterfell.zinc.model.DocumentField;
import io.github.sixcrabs.winterfell.zinc.support.GsonUtil;
import com.google.gson.JsonObject;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class UpdateMapping extends GenericResultAbstractZincAction {

    protected UpdateMapping(Builder builder) {
        super(builder);
        this.payload = GsonUtil.make("properties", builder.properties);
    }

    /**
     * request method
     *
     * @return
     */
    @Override
    public HttpRequestMethod getRequestMethod() {
        return HttpRequestMethod.PUT;
    }

    @Override
    protected String buildURI() {
        return "/api/" + indexName + "/_mapping";
    }

    public static class Builder extends AbstractZincAction.Builder<UpdateMapping, Builder> {

        public Builder() {
            setHeader("accept", "application/json");
            setHeader("content-type", "application/json");
        }

        private JsonObject properties = new JsonObject();

        public Builder addField(DocumentDateTimeField documentDateTimeField) {
            this.properties.add(documentDateTimeField.getName(), documentDateTimeField.toJsonObject());
            return this;
        }

        public Builder addField(DocumentField field) {
            this.properties.add(field.getName(), field.toJsonObject());
            return this;
        }

        @Override
        public UpdateMapping build() {
            return new UpdateMapping(this);
        }
    }
}