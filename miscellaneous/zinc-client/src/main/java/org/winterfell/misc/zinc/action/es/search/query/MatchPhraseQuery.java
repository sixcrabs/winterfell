package org.winterfell.misc.zinc.action.es.search.query;

import org.winterfell.misc.zinc.action.es.search.query.core.AbstractSingleFieldQuery;
import org.winterfell.misc.zinc.action.es.search.query.core.QueryType;
import org.winterfell.misc.zinc.support.GsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

import static org.winterfell.misc.zinc.support.ZincUtil.isNotBlank;

/**
 * <p>
 * <code>
 * <pre>
 *     "match_phrase": {
 *       "field": {
 *         "analyzer": "consequat irure do",
 *         "boost": 68277738.07412857,
 *         "query": "eu nisi consectetur commodo"
 *       }
 *     }
 *     </pre>
 * </code>
 * OR
 * <code>
 * <pre>
 *         "match_phrase": {
 *             "field": "query string"
 *         }
 *     </pre>
 * </code>
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/16
 */
public class MatchPhraseQuery extends AbstractSingleFieldQuery {


    private String analyzer;

    public MatchPhraseQuery(String fieldName, String queryText) {
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
        if (isNotBlank(analyzer) || boost != null) {
            inner = GsonUtil.make("query", queryText.toLowerCase(Locale.ROOT));
            if (isNotBlank(analyzer)) {
                inner.getAsJsonObject().add("analyzer", new JsonPrimitive(analyzer));
            }
            if (boost != null) {
                inner.getAsJsonObject().add("boost", new JsonPrimitive(boost));
            }
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
        return QueryType.match_phrase;
    }
}
