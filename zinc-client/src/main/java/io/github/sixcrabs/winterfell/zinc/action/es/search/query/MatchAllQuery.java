package io.github.sixcrabs.winterfell.zinc.action.es.search.query;

import io.github.sixcrabs.winterfell.zinc.action.es.search.query.core.AbstractQuery;
import io.github.sixcrabs.winterfell.zinc.action.es.search.query.core.QueryType;
import com.google.gson.JsonObject;

/**
 * <p>
 * "match_all": {}
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/16
 */
public class MatchAllQuery extends AbstractQuery {

    /**
     * query 类型
     *
     * @return
     */
    @Override
    public QueryType getType() {
        return QueryType.match_all;
    }

    /**
     * 实现内部对象的组织
     *
     * @return
     */
    @Override
    protected JsonObject internalJsonObject() {
        return new JsonObject();
    }
}