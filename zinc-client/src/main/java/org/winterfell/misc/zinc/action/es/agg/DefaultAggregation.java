package org.winterfell.misc.zinc.action.es.agg;

import org.winterfell.misc.zinc.support.GsonUtil;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * <p>
 * 通用的统计实现类
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/6/5
 */
public class DefaultAggregation extends AbstractAggregation {


    private final Map<String, Object> params;

    protected DefaultAggregation(Builder builder) {
        super(builder);
        this.params = builder.getParams();
    }

    /**
     * 实现内部对象的组织
     *
     * @return
     */
    @Override
    protected JsonObject internalJsonObject() {
        JsonObject jsonObject = GsonUtil.make("field", this.aggField);
        if (this.params != null && !this.params.isEmpty()) {
            return GsonUtil.merge(jsonObject, GsonUtil.gson.toJsonTree(this.params).getAsJsonObject());
        }
        return jsonObject;
    }

    public static class Builder extends AbstractAggregation.Builder<DefaultAggregation, Builder> {

        protected Map<String, Object> params;

        public Map<String, Object> getParams() {
            return params;
        }

        public Builder setParams(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public Builder(String fieldName) {
            this.setAggField(fieldName);
        }

        @Override
        public DefaultAggregation build() {
            return new DefaultAggregation(this);
        }
    }
}
