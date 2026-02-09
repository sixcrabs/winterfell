package io.github.sixcrabs.winterfell.indigo.renderer.ext.pptx.render.policy.impl;

import com.google.auto.service.AutoService;
import io.github.sixcrabs.winterfell.indigo.renderer.ext.pptx.render.RenderContext;
import io.github.sixcrabs.winterfell.indigo.renderer.ext.pptx.render.policy.AbstractSsRenderPolicy;
import io.github.sixcrabs.winterfell.indigo.renderer.ext.pptx.render.policy.SsRenderPolicy;

/**
 * 列表渲染策略
 *
 * @author alex
 * @version v1.0 2020/11/19
 */
@AutoService(SsRenderPolicy.class)
public class SsNumberingRenderPolicy extends AbstractSsRenderPolicy<Object> {


    /**
     * 实现渲染
     *
     * @param context
     * @throws Exception
     */
    @Override
    public void doRender(RenderContext<Object> context) throws Exception {
        logger.info("渲染列表数据...");
    }
}