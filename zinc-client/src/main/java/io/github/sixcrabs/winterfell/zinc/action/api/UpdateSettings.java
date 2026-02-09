package io.github.sixcrabs.winterfell.zinc.action.api;


import io.github.sixcrabs.winterfell.zinc.action.AbstractZincAction;
import io.github.sixcrabs.winterfell.zinc.action.GenericResultAbstractZincAction;
import io.github.sixcrabs.winterfell.zinc.http.HttpRequestMethod;

/**
 * <p>
 * update settings
 * </p>
 * <pre>
 * <code>
 *     {
 *     "analysis": {
 *         "analyzer": {
 *             "default": {
 *                 "type": "standard"
 *             },
 *             "my_analyzer": {
 *                 "tokenizer": "standard",
 *                 "char_filter": [
 *                     "my_mappings_char_filter"
 *                 ]
 *             }
 *         },
 *         "char_filter": {
 *             "my_mappings_char_filter": {
 *                 "type": "mapping",
 *                 "mappings": [
 *                     ":) =&gt; _happy_",
 *                     ":( =&gt; _sad_"
 *                 ]
 *             }
 *         }
 *     }
 * }
 * </code>
 * </pre>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class UpdateSettings extends GenericResultAbstractZincAction {

    public UpdateSettings() {
    }

    public UpdateSettings(Builder builder) {
        super(builder);
    }

    @Override
    protected String buildURI() {
        return "/es/" + indexName + "/_settings";
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

    public static class Builder extends AbstractZincAction.Builder<UpdateSettings, Builder> {

        @Override
        public UpdateSettings build() {
            return new UpdateSettings(this);
        }
    }
}