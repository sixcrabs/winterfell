package org.winterfell.misc.zinc.action.es.search.query.core;

import com.google.gson.JsonObject;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/16
 */
public interface Query {


    /**
     * query 类型
     * @return
     */
    QueryType getType();


    /**
     * json object
     * @return
     */
    JsonObject toJson();



}
