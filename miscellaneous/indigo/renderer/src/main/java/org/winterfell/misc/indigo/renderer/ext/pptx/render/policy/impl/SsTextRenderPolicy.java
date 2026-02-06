package org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.impl;

import com.google.auto.service.AutoService;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.RenderContext;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.AbstractSsRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.SsRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.impl.SsRunTemplate;

import java.util.Objects;

/**
 * 纯文本类渲染策略
 * TBD：
 *
 * @author alex
 * @version v1.0 2020/11/18
 */
@AutoService(SsRenderPolicy.class)
public class SsTextRenderPolicy extends AbstractSsRenderPolicy<Object> {

    /**
     * 实现渲染
     *
     * @param context
     * @throws Exception
     */
    @Override
    public void doRender(RenderContext<Object> context) throws Exception {
        SsRunTemplate template = (SsRunTemplate) context.getElementTemplate();
        XSLFTextRun textRun = template.getTextRun();
        String text = context.getData().toString();
        textRun.setText(textRun.getRawText().replace(template.getSource(), text));
    }

    /**
     * 数据验证
     *
     * @param data
     * @return
     */
    @Override
    protected boolean validate(Object data) {
        return !Objects.isNull(data);
    }
}