package org.winterfell.misc.zinc.action.es.search.query;

import org.winterfell.misc.zinc.action.es.search.query.core.AbstractSingleFieldQuery;
import org.winterfell.misc.zinc.action.es.search.query.core.QueryType;
import org.winterfell.misc.zinc.support.GsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

/**
 * <p>
 * <code>
 *     <pre>
 *          "prefix": {
 *             "field": "qu"
 *         }
 *     </pre>
 * </code>
 *
 * OR
 *
 * <code>
 *     <pre>
 *         "prefix": {
 *          "field": {
 *              "boost": 2.3,
 *              "value": "dolore Excepteur labore"
 *           }
 *     }
 *     </pre>
 * </code>
 *
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/17
 */
public class PrefixQuery extends AbstractSingleFieldQuery {

    public PrefixQuery(String fieldName, String queryText) {
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
            inner = GsonUtil.make("value", queryText.toLowerCase(Locale.ROOT));
            inner.getAsJsonObject().add("boost", new JsonPrimitive(boost));
        } else {
            inner = new JsonPrimitive(this.queryText.toLowerCase(Locale.ROOT));
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
        return QueryType.prefix;
    }
}
