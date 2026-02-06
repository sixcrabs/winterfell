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
 * <pre>
 *         "fuzzy": {
 *              "field": {
 *                  "boost": 94817590.61742249,
 *                  "fuzziness": {},
 *                   "prefix_length": -64281933.531116664,
 *                    "value": "enim "
 *                   }
 *           }
 *
 *     </pre>
 * </code>
 * <p>
 * OR
 *
 * <code>
 * <pre>
 *         "fuzzy": {
 *             "field": "xxx"
 *         }
 *     </pre>
 * </code>
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/17
 */
public class FuzzyQuery extends AbstractSingleFieldQuery {

    /**
     * The number of initial characters which will not be “fuzzified”.
     * This helps to reduce the number of terms which must be examined. Defaults to 0.
     */
    private Integer prefixLength;

    protected FuzzyQuery(String fieldName, String queryText) {
        super(fieldName, queryText);
    }

    public Integer getPrefixLength() {
        return prefixLength;
    }

    public FuzzyQuery setPrefixLength(Integer prefixLength) {
        this.prefixLength = prefixLength;
        return this;
    }

    /**
     * 实现内部对象的组织
     *
     * @return
     */
    @Override
    protected JsonObject internalJsonObject() {
        JsonElement inner;
        if (boost != null || prefixLength != null) {
            inner = GsonUtil.make("value", queryText);
            if (boost != null) {
                inner.getAsJsonObject().add("boost", new JsonPrimitive(boost));
            }
            if (prefixLength != null) {
                inner.getAsJsonObject().add("prefix_length", new JsonPrimitive(prefixLength));
            }
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
        return QueryType.fuzzy;
    }
}
