package org.winterfell.misc.zinc.action.es.search.query;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/4/23
 */
class BoolQueryTest {

    @Test
    void toJson() {

        BoolQuery boolQuery = BoolQuery.of(Lists.newArrayList(
                new TermQuery("name", "alex").setIgnoreCase(true),
                new MatchQuery("address", "n")
        )).addShould(Lists.newArrayList(
                new RangeQuery("age").setLowerValue(1).setUpperValue(100)
        ));
        JsonObject jsonObject = boolQuery.toJson();
        System.out.println(jsonObject.toString());

    }
}