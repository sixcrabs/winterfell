package org.winterfell.misc.indigo.renderer.ext.pptx.render.policy;

import org.winterfell.misc.indigo.renderer.ext.pptx.SlideShowTemplate;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsElementTemplate;

/**
 * 渲染策略接口
 * @author alex
 * @version v1.0 2020/11/18
 */
public interface SsRenderPolicy {

    /**
     * 渲染核心方法
     * @param eleTemplate 元素模板
     * @param data        数据
     * @param template    主模板
     */
    void render(SsElementTemplate eleTemplate, Object data, SlideShowTemplate template);
}