package org.winterfell.misc.indigo.renderer.ext.pptx.template.impl;

import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.SsRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.SsRenderPolicyFactory;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.impl.SsPictureRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsElementTemplate;

/**
 * 图片模板
 * 用于占位符图片替换!
 *
 * @author alex
 * @version v1.0 2020/11/18
 */
public class SsPictureTemplate extends SsElementTemplate {

    private XSLFPictureData pictureData;

    public XSLFPictureData getPictureData() {
        return pictureData;
    }

    public SsPictureTemplate setPictureData(XSLFPictureData pictureData) {
        this.pictureData = pictureData;
        return this;
    }

    @Override
    public SsRenderPolicy findPolicy() {
        return SsRenderPolicyFactory.INSTANCE.get(SsPictureRenderPolicy.class);
    }
}