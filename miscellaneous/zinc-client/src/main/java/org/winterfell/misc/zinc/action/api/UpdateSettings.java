package org.winterfell.misc.zinc.action.api;


import org.winterfell.misc.zinc.action.AbstractZincAction;
import org.winterfell.misc.zinc.action.GenericResultAbstractZincAction;
import org.winterfell.misc.zinc.http.HttpRequestMethod;

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
 *                     ":) => _happy_",
 *                     ":( => _sad_"
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
//        this.payload =
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
