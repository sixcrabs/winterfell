package org.winterfell.misc.indigo.renderer.ext.docx;

import com.deepoove.poi.data.RenderData;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 对象类型 render data
 * @author alex
 * @version v1.0 2020/9/14
 */
public class ObjectsRenderData implements RenderData {

    /**
     * real value, not primitive
     */
    private Object value;

    private ObjectsRenderData(Object value) {
        this.value = value;
    }

    public static ObjectsRenderData of(@NonNull Object value) {
        return new ObjectsRenderData(value);
    }

    public Object getValue() {
        return value;
    }
}