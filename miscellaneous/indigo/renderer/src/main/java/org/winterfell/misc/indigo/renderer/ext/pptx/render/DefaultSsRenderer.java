package org.winterfell.misc.indigo.renderer.ext.pptx.render;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterfell.misc.indigo.renderer.ext.pptx.SlideShowTemplate;
import org.winterfell.misc.indigo.renderer.ext.pptx.ex.SsRenderException;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.SsRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsElementTemplate;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsMetaTemplate;

import java.util.List;
import java.util.Objects;

/**
 * 默认渲染实现
 * TODO
 *
 * @author alex
 * @version v1.0 2020/11/17
 */
public class DefaultSsRenderer implements SsRenderer {

    private static Logger logger = LoggerFactory.getLogger(DefaultSsRenderer.class);

    public DefaultSsRenderer() {
    }

    /**
     * render {@link SlideShowTemplate}
     * <p>
     * - 获取到Template中的所有 ElementTemplate
     * - 遍历 ElementTemplate 根据类型的不同 获取到对应的 renderPolicy
     * - 组织成 renderContext 传入 对应的 policy中进行 渲染
     *
     * @param template ppt模板
     * @param root     数据
     */
    @Override
    public void render(SlideShowTemplate template, Object root) {
        Objects.requireNonNull(template, "Template must not be null.");
        Objects.requireNonNull(root, "Data root must not be null");

        logger.info("start rendering...");
        SsRenderDataCompute renderDataCompute = new DefaultSsRenderDataCompute(root, false);
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            List<SsMetaTemplate> eleTemplates = template.getEleTemplates();
            for (SsMetaTemplate metaTemplate : eleTemplates) {
                SsElementTemplate elementTemplate = (SsElementTemplate) metaTemplate;
                SsRenderPolicy policy = elementTemplate.findPolicy();
                policy.render(elementTemplate, renderDataCompute.compute(elementTemplate.getTagName()), template);
            }
        } catch (Exception e) {
            throw new SsRenderException(e.getLocalizedMessage());
        } finally {
            watch.stop();
        }
        logger.info("Successfully Render template in {} milliseconds", watch.getTime());

    }
}