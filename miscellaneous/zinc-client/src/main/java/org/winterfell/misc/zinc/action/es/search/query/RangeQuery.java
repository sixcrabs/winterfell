package org.winterfell.misc.zinc.action.es.search.query;

import org.winterfell.misc.zinc.action.es.search.query.core.AbstractQuery;
import org.winterfell.misc.zinc.action.es.search.query.core.QueryType;
import org.winterfell.misc.zinc.support.GsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Objects;

import static org.winterfell.misc.zinc.support.ZincUtil.isNotBlank;

/**
 * <p>
 * <code>
 * <pre>
 *         "field": {
 *                 "gt": 111,
 *                 "gte": 222,
 *                 "lt": "2015-01-01",
 *                 "lte": "2022-01-01",
 *                 "format": "2006-01-02T15:04:05Z07:00",
 *                 "time_zone": "UTC",
 *                 "boost": 1.0
 *             }
 *     </pre>
 * </code>
 * </p>
 * The range query supported field type `numeric` and `date`
 *
 * @author Alex
 * @version v1.0 2022/11/17
 */
public class RangeQuery extends AbstractQuery {

    private final String fieldName;

    /**
     * 得分权重
     */
    private Float boost;

    /**
     * "2006-01-02T15:04:05Z07:00";
     */
    private String dateFormat;

    /**
     * "UTC";
     */
    private String timeZone;

    /**
     * 下限值
     */
    private Object lowerValue;

    /**
     * 上限值
     */
    private Object upperValue;

    /**
     * gte
     */
    private boolean includeLower;

    /**
     * lte
     */
    private boolean includeUpper;

    public RangeQuery(String fieldName) {
        this.fieldName = fieldName;
    }

    public Float getBoost() {
        return boost;
    }

    public RangeQuery setBoost(Float boost) {
        this.boost = boost;
        return this;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public RangeQuery setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public RangeQuery setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public Object getLowerValue() {
        return lowerValue;
    }

    public RangeQuery setLowerValue(Object lowerValue) {
        this.lowerValue = lowerValue;
        return this;
    }

    public Object getUpperValue() {
        return upperValue;
    }

    public RangeQuery setUpperValue(Object upperValue) {
        this.upperValue = upperValue;
        return this;
    }

    public boolean isIncludeLower() {
        return includeLower;
    }

    public RangeQuery setIncludeLower(boolean includeLower) {
        this.includeLower = includeLower;
        return this;
    }

    public boolean isIncludeUpper() {
        return includeUpper;
    }

    public RangeQuery setIncludeUpper(boolean includeUpper) {
        this.includeUpper = includeUpper;
        return this;
    }

    /**
     * 实现内部对象的组织
     *
     * @return
     */
    @Override
    protected JsonObject internalJsonObject() {
        JsonObject inner = new JsonObject();
        if (boost != null) {
            inner.add("boost", new JsonPrimitive(boost));
        }
        if (lowerValue != null) {
            inner.add(isIncludeLower() ? "gte" : "gt",
                    GsonUtil.isNumber(lowerValue) ? new JsonPrimitive(Objects.requireNonNull(GsonUtil.toNumber(lowerValue))) : new JsonPrimitive(lowerValue.toString()));
        }
        if (upperValue != null) {
            inner.add(isIncludeUpper() ? "lte" : "lt",
                    GsonUtil.isNumber(upperValue) ? new JsonPrimitive(Objects.requireNonNull(GsonUtil.toNumber(upperValue))) : new JsonPrimitive(upperValue.toString()));
        }
        if (isNotBlank(this.dateFormat)) {
            inner.add("format", new JsonPrimitive(dateFormat));
        }
        if (isNotBlank(this.timeZone)) {
            inner.add("time_zone", new JsonPrimitive(timeZone));
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
        return QueryType.range;
    }
}
