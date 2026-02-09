package org.winterfell.misc.indigo.renderer.ext.pptx.render;


import org.winterfell.misc.indigo.renderer.ext.pptx.SlideShowTemplate;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsElementTemplate;

/**
 * 渲染的上下文
 *
 * @author alex
 * @version v1.0 2020/11/18
 */
public class RenderContext<T> {

    /**
     * 对应的要素模板
     */
    private SsElementTemplate elementTemplate;

    /**
     * 当前 tag 对应的数据
     */
    private T data;

    /**
     * 主模板
     */
    private SlideShowTemplate template;


    public RenderContext(SsElementTemplate eleTemplate, T data, SlideShowTemplate template) {
        this.elementTemplate = eleTemplate;
        this.data = data;
        this.template = template;
    }

    public SsElementTemplate getElementTemplate() {
        return elementTemplate;
    }

    public T getData() {
        return data;
    }

    public SlideShowTemplate getTemplate() {
        return template;
    }

    public String getTagSource() {
        return this.elementTemplate.getSource();
    }
}