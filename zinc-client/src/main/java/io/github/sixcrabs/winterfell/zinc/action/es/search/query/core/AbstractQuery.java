package io.github.sixcrabs.winterfell.zinc.action.es.search.query.core;

import io.github.sixcrabs.winterfell.zinc.support.GsonUtil;
import com.google.gson.JsonObject;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/16
 */
public abstract class AbstractQuery implements Query {

    /**
     * json object
     *
     * @return
     */
    @Override
    public JsonObject toJson() {
        return GsonUtil.make(this.getType().name(), internalJsonObject());
    }

    /**
     * 实现内部对象的组织
     * @return
     */
   protected abstract JsonObject internalJsonObject();
}