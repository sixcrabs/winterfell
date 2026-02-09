package io.github.sixcrabs.winterfell.zinc.action.es.agg;

import com.google.gson.JsonObject;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/6/5
 */
public interface Aggregation {

    /**
     * agg type
     * @return
     */
    AggType getType();

    /**
     * to agg json object
     * @return
     */
    JsonObject toJson();
}