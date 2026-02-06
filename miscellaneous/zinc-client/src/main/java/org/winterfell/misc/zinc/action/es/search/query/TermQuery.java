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
 * <pre>
 *         "term": {
 *             "field": "word"
 *         }
 *     </pre>
 * </code>
 * OR
 * <code>
 * <pre>
 *         "term": {
 *              "dolor17": {
 *                  "boost": 2.0,
 *                  "case_insensitive": false,
 *                  "value": "dol"
 *                  }
 *         }
 *     </pre>
 * </code>
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/16
 */
public class TermQuery extends AbstractSingleFieldQuery {


    private Boolean ignoreCase;

    public TermQuery(String fieldName, String queryText) {
        super(fieldName, queryText);
    }

    public Boolean isIgnoreCase() {
        return ignoreCase;
    }

    /**
     * 是否大小写敏感
     * true --> case_insensitive: true
     *
     * @param ignoreCase
     * @return
     */
    public TermQuery setIgnoreCase(Boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
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
        if (boost != null || ignoreCase != null) {
            inner = GsonUtil.make("value", queryText.toLowerCase(Locale.ROOT));
            if (boost != null) {
                inner.getAsJsonObject().add("boost", new JsonPrimitive(boost));
            }
            if (ignoreCase != null) {
                inner.getAsJsonObject().add("case_insensitive", new JsonPrimitive(ignoreCase));
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
        return QueryType.term;
    }
}
