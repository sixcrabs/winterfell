package org.winterfell.misc.zinc.action.api;

import org.winterfell.misc.zinc.action.AbstractZincAction;
import org.winterfell.misc.zinc.action.GenericResultAbstractZincAction;
import org.winterfell.misc.zinc.http.HttpRequestMethod;
import org.winterfell.misc.zinc.model.DocumentDateTimeField;
import org.winterfell.misc.zinc.model.DocumentField;
import org.winterfell.misc.zinc.support.GsonUtil;
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
