package org.winterfell.misc.zinc.action.es.search.query;

import org.winterfell.misc.zinc.action.es.search.query.core.AbstractQuery;
import org.winterfell.misc.zinc.action.es.search.query.core.Query;
import org.winterfell.misc.zinc.action.es.search.query.core.QueryType;
import org.winterfell.misc.zinc.support.GsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * <pre>
 *         <code>
 *          BoolQuery boolQuery = BoolQuery.of(Lists.newArrayList(
 *                 new TermQuery("name", "alex").setIgnoreCase(true),
 *                 new MatchQuery("address", "n")
 *         )).addShould(Lists.newArrayList(
 *                 new RangeQuery("age").setLowerValue(1).setUpperValue(100)
 *         ));
 *         </code>
 *     </pre>
 * </p>
 * <p>
 * es bool query
 *
 * <code>
 * <pre>
 *         "query": {
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
 *     }
 *     </pre>
 * </code>
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/16
 */
public class BoolQuery extends AbstractQuery {

    /**
     * must 数组
     */
    private final List<Query> must = new ArrayList<>(1);

    /**
     * must_not: []
     */
    private final List<Query> mustNot = new ArrayList<>(1);

    /**
     * "should": []
     */
    private final List<Query> should = new ArrayList<>(1);

    /**
     * "filter": []
     */
    private final List<Query> filter = new ArrayList<>(1);


    public static BoolQuery of() {
        return new BoolQuery();
    }

    public static BoolQuery of(List<Query> mustQueries) {
        BoolQuery boolQuery = new BoolQuery();
        boolQuery.addMust(mustQueries);
        return boolQuery;
    }

    /**
     * add queries to `must`
     *
     * @param queries
     * @return
     */
    public BoolQuery addMust(List<Query> queries) {
        this.must.addAll(queries.stream()
                .filter(query -> !query.getType().equals(QueryType.bool))
                .collect(Collectors.toList()));
        return this;
    }

    /**
     * add queries to `must_not`
     *
     * @param queries
     * @return
     */
    public BoolQuery addMustNot(List<Query> queries) {
        this.mustNot.addAll(queries.stream()
                .filter(query -> !query.getType().equals(QueryType.bool))
                .collect(Collectors.toList()));
        return this;
    }

    /**
     * add queries to `filter`
     *
     * @param queries
     * @return
     */
    public BoolQuery addFilter(List<Query> queries) {
        this.filter.addAll(queries.stream()
                .filter(query -> !query.getType().equals(QueryType.bool))
                .collect(Collectors.toList()));
        return this;
    }

    /**
     * add queries to `should`
     *
     * @param queries
     * @return
     */
    public BoolQuery addShould(List<Query> queries) {
        this.should.addAll(queries.stream()
                .filter(query -> !query.getType().equals(QueryType.bool))
                .collect(Collectors.toList()));
        return this;
    }


    public List<Query> getMust() {
        return must;
    }

    public List<Query> getMustNot() {
        return mustNot;
    }

    public List<Query> getShould() {
        return should;
    }

    public List<Query> getFilter() {
        return filter;
    }

    /**
     * 实现内部对象的组织
     *
     * @return
     */
    @Override
    protected JsonObject internalJsonObject() {
        JsonObject object = GsonUtil.make("must", toJsonArray(this.must));
        if (this.should.size() > 0) {
            object.add("should", toJsonArray(this.should));
        }
        if (this.mustNot.size() > 0) {
            object.add("must_not", toJsonArray(this.mustNot));
        }
        if (this.filter.size() > 0) {
            object.add("filter", toJsonArray(this.filter));
        }
        return object;
    }

    /**
     * queries to json array
     *
     * @param queries
     * @return
     */
    private JsonArray toJsonArray(List<Query> queries) {
        if (queries.size() == 0) {
            return new JsonArray();
        }
        JsonArray jsonArray = new JsonArray(queries.size());
        queries.forEach(q -> jsonArray.add(q.toJson()));
        return jsonArray;
    }

    /**
     * query 类型
     *
     * @return
     */
    @Override
    public QueryType getType() {
        return QueryType.bool;
    }
}
