package org.winterfell.misc.indigo.renderer.ext.docx;

import com.deepoove.poi.data.RenderData;

/**
 * <p>
 * 自定义渲染数据
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/3/23
 */
public interface DocxCustomRenderData<T> extends RenderData {

    /**
     * render value
     * @return
     */
    T getValue();

}