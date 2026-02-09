package org.winterfell.misc.zinc.action.es.search;

import org.winterfell.misc.zinc.ZincResult;
import org.winterfell.misc.zinc.support.CloneUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/11/2
 */
public class SearchResult extends ZincResult {

    public static final Logger logger = LoggerFactory.getLogger(SearchResult.class);

//    public static final String EXPLANATION_KEY = "_explanation";
//    public static final String HIGHLIGHT_KEY = "highlight";
//    public static final String SORT_KEY = "sort";

    public static final String[] PATH_TO_TOTAL = "hits/total/value".split("/");
    public static final String[] PATH_TO_MAX_SCORE = "hits/max_score".split("/");
    public static final String[] PATH_TO_HITS = "hits/hits".split("/");
    public static final String[] PATH_TO_AGGS = new String[]{"aggregations"};


    public SearchResult(Gson gson) {
        super(gson);
    }

    @Override
    public <T> T getSourceAsObject(Class<T> sourceType) {
        Hit<T> firstHit = getFirstHit(sourceType);
        if (Objects.isNull(firstHit)) {
            logger.warn("hit is null");
            return null;
        }
        return firstHit.getSource();
    }

    @Override
    public <T> List<T> getSourceAsObjectList(Class<T> sourceType) {
        List<Hit<T>> hits = getHits(sourceType);
        return hits.stream().map(Hit::getSource).collect(Collectors.toList());
    }

    /**
     * total
     *
     * @return
     */
    public Long getTotal() {
        Long total = null;
        JsonElement obj = getPath(PATH_TO_TOTAL);
        if (obj != null) {
            total = obj.getAsLong();
        }
        return total;
    }

    /**
     * max_score
     *
     * @return
     */
    public Float getMaxScore() {
        Float maxScore = null;
        JsonElement obj = getPath(PATH_TO_MAX_SCORE);
        if (obj != null && !obj.isJsonNull()) {
            maxScore = obj.getAsFloat();
        }
        return maxScore;
    }

    /**
     * get aggregations
     *
     * @return
     */
    public Aggregations getAggregations() {
        JsonElement obj = getPath(PATH_TO_AGGS);
        return new Aggregations(obj.isJsonNull() ? null : obj.getAsJsonObject());
    }

    public <T> Hit<T> getFirstHit(Class<T> sourceType) {
        Hit<T> hit = null;
        List<Hit<T>> hits = getHits(sourceType, true);
        if (!hits.isEmpty()) {
            hit = hits.get(0);
        }
        return hit;
    }

    public <T> List<Hit<T>> getHits(Class<T> sourceType) {
        return getHits(sourceType, true);
    }

    public <T> List<Hit<T>> getHits(Class<T> sourceType, boolean addEsMetadataFields) {
        return getHits(sourceType, false, addEsMetadataFields);
    }

    protected <T> List<Hit<T>> getHits(Class<T> sourceType, boolean returnSingle, boolean addEsMetadataFields) {
        List<Hit<T>> sourceList = new ArrayList<>();
        if (jsonObject != null) {
            JsonElement obj = getPath(PATH_TO_HITS);
            if (obj.isJsonObject()) {
                sourceList.add(extractHit(sourceType, obj, addEsMetadataFields));
            } else if (obj.isJsonArray()) {
                for (JsonElement hitElement : obj.getAsJsonArray()) {
                    sourceList.add(extractHit(sourceType, hitElement, addEsMetadataFields));
                    if (returnSingle) {
                        break;
                    }
                }
            }
        }
        return sourceList;
    }

    private <T> Hit<T> extractHit(Class<T> sourceType, JsonElement hitElement, boolean addEsMetadataFields) {
        Hit<T> hit = null;
        if (hitElement.isJsonObject()) {
            JsonObject hitObject = hitElement.getAsJsonObject();
            JsonObject source = hitObject.getAsJsonObject("_source");

            String index = hitObject.get("_index").getAsString();
            String type = hitObject.get("_type").getAsString();
            String id = hitObject.get("_id").getAsString();
            String timestamp = hitObject.get("@timestamp").getAsString();

            Double score = null;
            if (hitObject.has("_score") && !hitObject.get("_score").isJsonNull()) {
                score = hitObject.get("_score").getAsDouble();
            }

            if (addEsMetadataFields) {
                JsonObject clonedSource = null;
                for (MetaField metaField : META_FIELDS) {
                    JsonElement metaElement = hitObject.get(metaField.esFieldName);
                    if (metaElement != null) {
                        if (clonedSource == null) {
                            if (source == null) {
                                clonedSource = new JsonObject();
                            } else {
                                clonedSource = (JsonObject) CloneUtil.deepClone(source);
                            }
                        }
                        clonedSource.add(metaField.internalFieldName, metaElement);
                    }
                }
                if (clonedSource != null) {
                    source = clonedSource;
                }
            }

            hit = new Hit<T>(
                    sourceType,
                    source,
                    index,
                    type,
                    id,
                    score,
                    timestamp);

        }
        return hit;
    }

    public JsonElement getPath(String[] path) {
        JsonElement retval = null;
        if (jsonObject != null) {
            JsonElement obj = jsonObject;
            for (String component : path) {
                if (obj == null) {
                    break;
                }
                obj = ((JsonObject) obj).get(component);
            }
            retval = obj;
        }
        return retval;
    }

    /**
     * TODO
     * 用于映射统计结果
     */
    public class Aggregations {

        private JsonObject root;

        public Aggregations(JsonObject root) {
            this.root = root == null ? new JsonObject() : root;
        }

        public boolean isEmpty() {
            return this.root.isEmpty();
        }

        /**
         * 获取 sum/count/max/min/avg 等单个统计值
         * @param aggName
         * @return
         */
        public Double getMetricsValue(String aggName) {
            JsonObject object = this.root.getAsJsonObject(aggName);
            if (object == null) {
                return null;
            }
            if (object.isJsonNull()) {
                return null;
            }
            return object.get("value").getAsDouble();
        }


    }


    /**
     * Immutable class representing a search hit.
     *
     * @param <T> type of source
     * @author cihat keser
     */
    public class Hit<T> {

        protected final T source;
        //        public final Map<String, List<String>> highlight;
//        public final List<String> sort;
        protected final String index;
        protected final String type;
        protected final String id;
        protected final Double score;
        protected final String timestamp;

        public Hit(T source, String index, String type, String id, Double score, String timestamp) {
            this.source = source;
            this.index = index;
            this.type = type;
            this.id = id;
            this.score = score;
            this.timestamp = timestamp;
        }

        public Hit(Class<T> sourceType, JsonElement source, String index, String type, String id, Double score, String timestamp) {
            this.timestamp = timestamp;
            if (source == null) {
                this.source = null;
            } else {
                this.source = createSourceObject(source, sourceType);
            }
            this.index = index;
            this.type = type;
            this.id = id;
            this.score = score;
        }

        public T getSource() {
            return source;
        }

        public String getTimestamp() {
            return timestamp;
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    source,
                    index,
                    type,
                    id);
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

            Hit rhs = (Hit) obj;
            return Objects.equals(source, rhs.source)
                    && Objects.equals(index, rhs.index)
                    && Objects.equals(type, rhs.type)
                    && Objects.equals(id, rhs.id);
        }
    }
}
