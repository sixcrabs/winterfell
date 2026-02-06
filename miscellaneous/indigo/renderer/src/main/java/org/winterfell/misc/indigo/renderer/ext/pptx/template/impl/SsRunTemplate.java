package org.winterfell.misc.indigo.renderer.ext.pptx.template.impl;

import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.SsRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.SsRenderPolicyFactory;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.impl.SsNumberingRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.impl.SsPictureRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.impl.SsTableRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.impl.SsTextRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsElementTemplate;

import static org.winterfell.misc.indigo.renderer.ext.pptx.support.Constants.EMPTY_CHAR;

/**
 * 基础要素模板 可用于渲染
 * - 纯文本(`{{var}}`)
 * - 插入表格(`{{#var}}`)
 * - 插入图片(`{{@var}}`)
 * - 插入列表(`{{*var}}`)
 *
 * @author alex
 * @version v1.0 2020/11/19
 */
public class SsRunTemplate extends SsElementTemplate {

    private XSLFTextRun textRun;

    /**
     * 标签模板所在的 外部 shape
     */
    private XSLFShape shape;

    public XSLFTextRun getTextRun() {
        return textRun;
    }

    public SsRunTemplate setTextRun(XSLFTextRun textRun) {
        this.textRun = textRun;
        return this;
    }

    public XSLFShape getShape() {
        return shape;
    }

    public SsRunTemplate setShape(XSLFShape shape) {
        this.shape = shape;
        return this;
    }

    /**
     * find policy
     *
     * @return
     */
    @Override
    public SsRenderPolicy findPolicy() {
        // sign 的不同 返回不同的 渲染策略对象
        switch (sign) {
            case '@':
                return SsRenderPolicyFactory.INSTANCE.get(SsPictureRenderPolicy.class);
            case '#':
                return SsRenderPolicyFactory.INSTANCE.get(SsTableRenderPolicy.class);
            case '*':
                return SsRenderPolicyFactory.INSTANCE.get(SsNumberingRenderPolicy.class);
            case EMPTY_CHAR:
            default:
                return SsRenderPolicyFactory.INSTANCE.get(SsTextRenderPolicy.class);
        }
    }
}