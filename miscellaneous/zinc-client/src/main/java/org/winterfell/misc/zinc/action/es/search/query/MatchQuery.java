package org.winterfell.misc.zinc.action.es.search.query;

import org.winterfell.misc.zinc.action.es.search.query.core.AbstractSingleFieldQuery;
import org.winterfell.misc.zinc.support.GsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.winterfell.misc.zinc.action.es.search.query.core.QueryType;

import static org.winterfell.misc.zinc.support.ZincUtil.isNotBlank;

/**
 * <p>
 * <code>
 * <pre>
 *     "match": {
 *         "field": {
 *           "analyzer": "cu",
 *           "boost": 14565499.830775648,
 *           "fuzziness": {},
 *           "operator": "ut",
 *           "prefix_length": 99492889.3190794,
 *           "query": "query string"
 *         }
 *       }
 *      </pre>
 * </code>
 * OR
 * <code>
 * <pre>
 *        "match": {
 *          "field": "query string"
 *         }
 *         </pre>
 * </code>
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/16
 */
public class MatchQuery extends AbstractSingleFieldQuery {

    private String analyzer;

    private String operator;

    public MatchQuery(String fieldName, String queryText) {
        super(fieldName, queryText);
    }

    public MatchQuery setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    public MatchQuery setOperator(String operator) {
        this.operator = operator;
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
        if (isNotBlank(analyzer) || isNotBlank(operator)) {
            inner = GsonUtil.make("query", queryText);
            if (isNotBlank(analyzer)) {
                inner.getAsJsonObject().add("analyzer", new JsonPrimitive(analyzer));
            }
            if (isNotBlank(operator)) {
                inner.getAsJsonObject().add("operator", new JsonPrimitive(operator));
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
        return QueryType.match;
    }
}
