package org.winterfell.misc.indigo.renderer.ext.pptx.render.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterfell.misc.indigo.renderer.ext.pptx.SlideShowTemplate;
import org.winterfell.misc.indigo.renderer.ext.pptx.ex.SsRenderException;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.RenderContext;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsElementTemplate;

/**
 *
 *
 * @author alex
 * @version v1.0 2020/11/18
 */
public abstract class AbstractSsRenderPolicy<T> implements SsRenderPolicy {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 渲染核心方法
     *
     * @param eleTemplate 元素模板
     * @param data        数据
     * @param template    主模板
     */
    @SuppressWarnings("unchecked")
    @Override
    public void render(SsElementTemplate eleTemplate, Object data, SlideShowTemplate template) {
        // type safe 转换为对应模板的数据类型
        T model = null;
        try {
            model = (T) data;
        } catch (ClassCastException e) {
            throw new SsRenderException("Error Render Data format for template: " + eleTemplate.getSource(), e);
        }

        RenderContext<T> context = new RenderContext<T>(eleTemplate, model, template);
        try {
            // 验证数据
            if (!validate(model)) {
                logger.error("tag of [{}] is invalid, will be skipped", context.getTagSource());
                return;
            }
            // 执行渲染
            beforeRender(context);
            doRender(context);
            afterRender(context);
        } catch (Exception e) {
            handleRenderException(context, e);
        }
    }

    /**
     * 子类可覆盖来自定义异常处理
     *
     * @param context
     * @param e
     */
    protected void handleRenderException(RenderContext<T> context, Exception e) {
        throw new SsRenderException("Render template " + context.getElementTemplate() + " failed! cause: " + e.getLocalizedMessage());
    }

    /**
     * 实现渲染
     *
     * @param context
     * @throws Exception
     */
    public abstract void doRender(RenderContext<T> context) throws Exception;

    /**
     * 渲染前执行
     *
     * @param context
     */
    protected void beforeRender(RenderContext<T> context) {
    }

    /**
     * 渲染后执行
     *
     * @param context
     */
    protected void afterRender(RenderContext<T> context) {
    }

    /**
     * 数据验证
     *
     * @param data
     * @return
     */
    protected boolean validate(T data) {
        return true;
    }
}