package org.winterfell.misc.zinc.action.es.search;

import org.winterfell.misc.zinc.action.AbstractZincAction;
import org.winterfell.misc.zinc.action.es.AbstractEsZincAction;
import org.winterfell.misc.zinc.action.es.agg.Aggregation;
import org.winterfell.misc.zinc.action.es.search.query.core.Query;
import org.winterfell.misc.zinc.http.HttpRequestMethod;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.*;

/**
 * <p>
 * https://zincsearch-docs.zinc.dev/api-es-compatible/search/search/types/
 * 格式：
 * <pre>
 *     <code>
 *         {
 *     "query": {
 *         "bool": {
 *             "must": [
 *                 { "match": { "City":  "paris" }},
 *                 { "match": { "Medal": "gold" }}
 *             ],
 *             "should": [],
 *             "must_not": [],
 *             "filter": [
 *                 { "term":  { "Country": "ger" }},
 *                 { "range": { "@timestamp": { "gte": "2015-01-01", "format": "2006-01-02" }}}
 *             ]
 *         }
 *     },
 *     "_source": [],
 *     "sort": "@timestamp",
 *     "size": 10,
 *     "aggs": {}
 * }
 *     </code>
 * </pre>
 * </p>
 *
 * <p>
 * sort 支持 array 或 string 模式
 * eg：
 * {
 * "sort": "@timestamp"
 * }
 * 默认正序，倒序：
 * {
 * "sort": ["-@timestamp","xxx"]
 * }
 * </p>
 * source 支持 array 或 string
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class Search extends AbstractEsZincAction<SearchResult> {

    /**
     * query
     */
    private Query query;
    /**
     * 排序
     */
    private List<Sort> sortList;

    /**
     * _source: []
     */
    protected List<String> sourcePatternList;

    /**
     * 分页参数 - 起始序号
     */
    private Integer from;
    /**
     * 分页参数 - 大小
     */
    private int size;

    /**
     * aggs
     */
    private JsonObject aggs;


    protected Search(Builder builder) {
        this.query = builder.query;
        this.sortList = builder.sortList;
        this.sourcePatternList = builder.sourcePatternList;
        this.from = builder.from;
        this.size = builder.size;
        this.aggs = builder.aggs;
        this.indexName = builder.getIndexName();
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
     * /es/:index/_search
     *
     * @return
     */
    @Override
    protected String buildURI() {
        String buildURI = super.buildURI();
        return buildURI.endsWith("/") ? buildURI : buildURI.concat("/") + "_search";
    }

    @Override
    public String getData(Gson gson) {
        JsonObject root = new JsonObject();
        JsonObject queryObject = query.toJson();
        // 处理 sort
        if (!sortList.isEmpty()) {
            JsonArray sortArray = new JsonArray(sortList.size());
            sortList.forEach(sort -> sortArray.add(new JsonPrimitive(sort.toValue())));
            root.add("sort", sortArray);
        }
        // 处理 _source
        if (!sourcePatternList.isEmpty()) {
            JsonArray sourceArray = new JsonArray(sourcePatternList.size());
            sourcePatternList.forEach(source -> sourceArray.add(new JsonPrimitive(source)));
            root.add("_source", sourceArray);
        }
        // 处理分页参数
        if (from != null) {
            root.add("from", new JsonPrimitive(from));
        }
        root.add("size", new JsonPrimitive(size));

        root.add("query", queryObject);

        if (!aggs.isEmpty()) {
            root.add("aggs", aggs);
        }
        return gson.toJson(root);
    }

    public String getIndexName() {
        return this.indexName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), query, sortList, sourcePatternList, from, size);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        Search rhs = (Search) obj;
        return super.equals(obj)
                && Objects.equals(query, rhs.query)
                && Objects.equals(sortList, rhs.sortList)
                && Objects.equals(sourcePatternList, rhs.sourcePatternList)
                && Objects.equals(from, rhs.from)
                && Objects.equals(size, rhs.size);
    }


    public static class Builder extends AbstractZincAction.Builder<Search, Builder> {

        protected Query query;
        protected List<Sort> sortList = new LinkedList<>();
        protected List<String> sourcePatternList = new ArrayList<>();
        // 默认返回前 1000 条记录
        protected int size = 1000;
        // 用于分页
        protected Integer from;

        // 用于统计
        protected JsonObject aggs = new JsonObject();

        public Builder(Query query) {
            this.query = query;
        }

        public Builder addSort(Sort sort) {
            sortList.add(sort);
            return this;
        }

        public Builder addAggregation(Aggregation aggregation) {
            JsonObject aggJson = aggregation.toJson();
            Set<String> keySet = aggJson.keySet();
            keySet.forEach(key -> aggs.add(key, aggJson.getAsJsonObject(key)));
            return this;
        }

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder setFrom(int from) {
            if (from > 0) {
                this.from = from;
            }
            return this;
        }

        /**
         * 添加返回的源字段名
         *
         * @param includePattern 返回包含的字段名 或 pattern
         * @return
         */
        public Builder addSourceIncludePattern(String includePattern) {
            sourcePatternList.add(includePattern);
            return this;
        }

        public Builder addSort(Collection<Sort> sorts) {
            sortList.addAll(sorts);
            return this;
        }

        @Override
        public Search build() {
            return new Search(this);
        }
    }
}
