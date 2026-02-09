package org.winterfell.misc.indigo.renderer.ext.docx;

import com.deepoove.poi.data.TextRenderData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.winterfell.misc.indigo.renderer.support.IndigoRenderException;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author alex
 * @version v1.0 2020/9/14
 */
public final class RenderDataHelper {

    /**
     * normal map to tpl data
     *
     * @param payload 载荷数据
     * @param clazz   render data class
     * @param <T>     T
     * @return Map with T
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> toTplData(@NonNull Map<String, ?> payload, @NonNull Class<T> clazz) {

        if (ObjectsRenderData.class.equals(clazz)) {
            Map<String, ObjectsRenderData> tplData = new HashMap<>(payload.size());
            payload.keySet().forEach(key -> tplData.put(key, ObjectsRenderData.of(payload.get(key))));
            return (Map<String, T>) tplData;
        } else if (TextRenderData.class.equals(clazz)) {
            Map<String, TextRenderData> tplData = new HashMap<>(payload.size());
            payload.keySet().forEach(key -> tplData.put(key, new TextRenderData(String.valueOf(payload.get(key)))));
            return (Map<String, T>) tplData;
        }
        throw new IndigoRenderException("not support class of [{}]", clazz.getName());
    }
}