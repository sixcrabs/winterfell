package org.winterfell.misc.zinc.action.es.agg;

import org.winterfell.misc.zinc.support.GsonUtil;
import com.google.gson.JsonObject;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/6/5
 */
public abstract class AbstractAggregation implements Aggregation {


    protected final String aggField;

    protected final AggType aggType;

    protected AbstractAggregation(Builder builder) {
        this.aggField = builder.getAggField();
        this.aggType = builder.getAggType();
    }

    /**
     * agg type
     *
     * @return
     */
    @Override
    public AggType getType() {
        return this.aggType;
    }

    /**
     * to agg json object
     *
     * @return
     */
    @Override
    public JsonObject toJson() {
        // name 自动拼接生成
        return GsonUtil.make(String.format("%s_%s", this.aggType.name(), this.aggField),
                GsonUtil.make(this.aggType.name(), internalJsonObject()));
    }

    /**
     * 实现内部对象的组织
     *
     * @return
     */
    protected abstract JsonObject internalJsonObject();

    protected static abstract class Builder<T extends Aggregation, K> {

        /**
         * 统计字段
         */
        protected String aggField;

        /**
         * 统计类型
         */
        protected AggType aggType;

        public String getAggField() {
            return aggField;
        }

        public Builder<T, K> setAggField(String aggField) {
            this.aggField = aggField;
            return this;
        }

        public AggType getAggType() {
            return aggType;
        }

        public Builder<T, K> setAggType(AggType aggType) {
            this.aggType = aggType;
            return this;
        }

        abstract public T build();

    }


}
