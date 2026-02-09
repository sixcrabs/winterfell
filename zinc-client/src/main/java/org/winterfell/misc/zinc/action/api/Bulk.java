package org.winterfell.misc.zinc.action.api;


import org.winterfell.misc.zinc.action.AbstractZincAction;
import org.winterfell.misc.zinc.action.BulkableZincAction;
import org.winterfell.misc.zinc.http.HttpRequestMethod;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.*;

import static org.winterfell.misc.zinc.support.GsonUtil.getJson;
import static org.winterfell.misc.zinc.support.ZincUtil.isNotBlank;

/**
 * <p>
 * 批量进行index 操作，可以同时 新增、更新、删除等
 * 参考： https://docs.zincsearch.com/api/document/bulk/
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class Bulk extends AbstractZincAction<BulkResult> {


    /**
     * 支持bulk的操作
     */
    protected Collection<BulkableZincAction> bulkableActions;

    public Bulk(Builder builder) {
        super(builder);
        indexName = builder.getIndexName();
        bulkableActions = builder.actions;
    }


    /**
     * 子类可以实现该方法修改请求url
     *
     * @return
     */
    @Override
    protected String buildURI() {
        return "/api/_bulk";
    }

    /**
     * request method
     *
     * @return
     */
    @Override
    public HttpRequestMethod getRequestMethod() {
        return HttpRequestMethod.POST;
    }

    /**
     * update/create:
     * { "index" : { "_index" : "test", "_id" : "1" } }
     * { "field1" : "value1" }
     *
     * delete:
     * { "delete" : { "_index" : "test", "_id" : "2" } }
     *
     * @param gson
     * @return
     */
    @Override
    public String getData(Gson gson) {
        StringBuilder sb = new StringBuilder();
        for (BulkableZincAction action : bulkableActions) {
            // write out the action-meta-data line
            // e.g.: { "index" : { "_index" : "test", "_id" : "1" } }
            Map<String, Map<String, String>> opMap = new LinkedHashMap<String, Map<String, String>>(1);

            Map<String, String> opDetails = new LinkedHashMap<String, String>(3);
            if (isNotBlank(action.getId())) {
                opDetails.put("_id", action.getId());
            }
            if (isNotBlank(action.getIndexName())) {
                opDetails.put("_index", action.getIndexName());
            }else {
                opDetails.put("_index", this.indexName);
            }

            opMap.put(action.getBulkMethodName(), opDetails);
            sb.append(gson.toJson(opMap, new TypeToken<Map<String, Map<String, String>>>() {
            }.getType()));
            sb.append("\n");

            // write out the action source/document line
            // e.g.: { "field1" : "value1" }
            Object source = action.getData(gson);
            if (source != null) {
                sb.append(getJson(gson, source));
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @SuppressWarnings("rawtypes")
    public static class Builder extends AbstractZincAction.Builder<Bulk, Builder> {

        private List<BulkableZincAction> actions = new LinkedList<BulkableZincAction>();

        /**
         * 默认索引名称
         */
        public Builder defaultIndexName(String index) {
            this.setIndexName(index);
            return this;
        }

        public Builder addAction(BulkableZincAction action) {
            this.actions.add(action);
            return this;
        }

        public Builder addAction(Collection<? extends BulkableZincAction> actions) {
            this.actions.addAll(actions);
            return this;
        }

        public int actionSize() {
            return this.actions.size();
        }

        @Override
        public Bulk build() {
            return new Bulk(this);
        }
    }
}
