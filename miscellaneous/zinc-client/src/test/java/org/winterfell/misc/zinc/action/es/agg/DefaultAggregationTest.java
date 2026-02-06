package org.winterfell.misc.zinc.action.es.agg;

import org.winterfell.misc.zinc.action.es.search.Search;
import org.winterfell.misc.zinc.action.es.search.query.MatchAllQuery;
import org.winterfell.misc.zinc.support.GsonUtil;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/6/5
 */
public class DefaultAggregationTest {




    @Test
    public void testInternalJsonObject() {

        Map<String, Object> map = new HashMap<>(3);
        map.put("weight_field", "Year");
        map.put("size", 100);
        JsonElement jsonElement = GsonUtil.gson().toJsonTree(map);
        System.out.println(jsonElement.toString());
    }

    @Test
    void testAgg() {

        DefaultAggregation.Builder builder = new DefaultAggregation.Builder("Year");
        builder.setAggType(AggType.min);
        DefaultAggregation aggregation = builder.build();
        Search search = new Search.Builder(new MatchAllQuery()).addAggregation(aggregation).build();
        String data = search.getData(GsonUtil.gson());
        System.out.println(data);

    }
}