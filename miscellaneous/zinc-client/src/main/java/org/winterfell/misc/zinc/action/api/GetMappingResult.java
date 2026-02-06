package org.winterfell.misc.zinc.action.api;

import org.winterfell.misc.zinc.action.ZincMessagedResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/17
 */
public class GetMappingResult extends ZincMessagedResult {
    public GetMappingResult(Gson gson) {
        super(gson);
    }

    /**
     * 获取 properties
     *
     * @param indexName
     * @return
     */
    public JsonObject getProperties(String indexName) {
        pathToResult = indexName.concat("/mappings/properties");
        return extractSource().get(0).getAsJsonObject();
    }

    /**
     * 获取某一个字段的描述
     * @param indexName 索引名称
     * @param fieldName 字段名称
     * @return
     */
    public JsonObject getSingleProperty(@Nonnull String indexName, @Nonnull String fieldName) {
        pathToResult = indexName.concat("/mappings/properties/").concat(fieldName);
        List<JsonElement> elements = extractSource();
        if (elements.size() == 0) {
            return new JsonObject();
        }
        return elements.get(0).getAsJsonObject();
    }

}
