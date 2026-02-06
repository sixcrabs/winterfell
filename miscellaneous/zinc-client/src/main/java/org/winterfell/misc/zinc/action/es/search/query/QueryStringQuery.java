package org.winterfell.misc.zinc.action.es.search.query;

import org.winterfell.misc.zinc.action.es.search.query.core.AbstractQuery;
import org.winterfell.misc.zinc.action.es.search.query.core.QueryType;
import org.winterfell.misc.zinc.support.GsonUtil;
import com.google.gson.JsonObject;

/**
 * <p>
 * The query language query allows humans to describe complex queries using a simple syntax.
 *
 * <code>
 * <pre>
 *         "query_string": {
 *             "query": "query string +other word +content:test"
 *         }
 *     </pre>
 * </code>
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/24
 */
public class QueryStringQuery extends AbstractQuery {

    private final String queryText;

    public QueryStringQuery(String queryText) {
        this.queryText = queryText;
    }

    /**
     * 实现内部对象的组织
     *
     * @return
     */
    @Override
    protected JsonObject internalJsonObject() {
        return GsonUtil.make("query", this.queryText);
    }

    /**
     * query 类型
     *
     * @return
     */
    @Override
    public QueryType getType() {
        return QueryType.query_string;
    }
}
