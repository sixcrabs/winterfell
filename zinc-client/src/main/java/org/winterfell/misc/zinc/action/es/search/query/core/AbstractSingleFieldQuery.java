package org.winterfell.misc.zinc.action.es.search.query.core;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/16
 */
public abstract class AbstractSingleFieldQuery extends AbstractQuery {

    protected final String fieldName;

    protected final String queryText;

    /**
     * 得分权重
     */
    protected Float boost;

    public AbstractSingleFieldQuery(String fieldName, String queryText) {
        this.fieldName = fieldName;
        this.queryText = queryText;
    }

    public Float getBoost() {
        return boost;
    }

    public AbstractSingleFieldQuery setBoost(Float boost) {
        this.boost = boost;
        return this;
    }
}
