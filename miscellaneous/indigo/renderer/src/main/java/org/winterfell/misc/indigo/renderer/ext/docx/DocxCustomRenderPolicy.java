package org.winterfell.misc.indigo.renderer.ext.docx;

import com.deepoove.poi.policy.RenderPolicy;

/**
 * <p>
 * 自定义docx文档渲染策略 可通过 SPI 实现扩展
 * </p>
 * @author alex
 * @version v1.0, 2020/3/24
 */
public interface DocxCustomRenderPolicy extends RenderPolicy {

    /**
     * 文档中标记 eg `{{my}}`
     * @return
     */
    String renderTag();

    /**
     * 注册为新的标签类型
     *
     * @return
     */
    Character renderPluginChar();

}