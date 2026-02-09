package org.winterfell.misc.zinc.action.es.search.query;

import org.winterfell.misc.zinc.action.es.search.query.core.AbstractQuery;
import org.winterfell.misc.zinc.action.es.search.query.core.QueryType;
import org.winterfell.misc.zinc.support.GsonUtil;
import com.google.gson.JsonObject;

/**
 * <p>
 * "exists": {
 * "field": "anim"
 * }
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/16
 */
@Deprecated
public class ExistsQuery extends AbstractQuery {

    private final String fieldName;

    protected ExistsQuery(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * 实现内部对象的组织
     *
     * @return
     */
    @Override
    protected JsonObject internalJsonObject() {
        return GsonUtil.make("field", this.fieldName);
    }

    /**
     * query 类型
     *
     * @return
     */
    @Override
    public QueryType getType() {
        return QueryType.exists;
    }
}
