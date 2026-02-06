package org.winterfell.misc.zinc.action.es.search.query;

import org.winterfell.misc.zinc.action.es.search.query.core.AbstractSingleFieldQuery;
import org.winterfell.misc.zinc.action.es.search.query.core.QueryType;
import org.winterfell.misc.zinc.support.GsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * <p>
 * <code>
 *     <pre>
 *          "wildcard": {
 *             "field": "qu*"
 *         }
 *     </pre>
 * </code>
 * OR
 *
 * <code>
 *     <pre>
 *          "wildcard": {
 *             "field": {
 *                 "value": "qu*",
 *                 "boost": 2.0
 *             }
 *         }
 *
 *     </pre>
 * </code>
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/17
 */
public class WildcardQuery extends AbstractSingleFieldQuery {

    public WildcardQuery(String fieldName, String queryText) {
        super(fieldName, queryText);
    }

    /**
     * 实现内部对象的组织
     *
     * @return
     */
    @Override
    protected JsonObject internalJsonObject() {
        JsonElement inner;
        if (boost != null) {
            inner = GsonUtil.make("value", queryText);
            inner.getAsJsonObject().add("boost", new JsonPrimitive(boost));
        } else {
            inner = new JsonPrimitive(this.queryText);
        }
        return GsonUtil.make(fieldName, inner);
    }

    /**
     * query 类型
     *
     * @return
     */
    @Override
    public QueryType getType() {
        return QueryType.wildcard;
    }
}
