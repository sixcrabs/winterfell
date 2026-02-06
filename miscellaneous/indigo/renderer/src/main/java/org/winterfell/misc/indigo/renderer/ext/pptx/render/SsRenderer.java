package org.winterfell.misc.indigo.renderer.ext.pptx.render;

import org.winterfell.misc.indigo.renderer.ext.pptx.SlideShowTemplate;

/**
 * 渲染接口
 * @author alex
 * @version v1.0 2020/11/17
 */
public interface SsRenderer {

    /**
     * render {@link SlideShowTemplate}
     *
     * @param template  ppt模板
     * @param root      数据
     */
    void render(SlideShowTemplate template, Object root);


}