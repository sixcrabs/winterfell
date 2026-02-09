package org.winterfell.misc.indigo.renderer.ext.pptx.render;


import org.winterfell.misc.indigo.renderer.ext.pptx.ex.ExpressionEvalException;
import org.winterfell.misc.indigo.renderer.ext.pptx.expression.DefaultEL;

/**
 * 默认实现
 * @author alex
 * @version v1.0 2020/11/18
 */
public class DefaultSsRenderDataCompute implements SsRenderDataCompute {

    private DefaultEL elObject;

    private boolean isStrict;

    public DefaultSsRenderDataCompute(Object root, boolean isStrict) {
        this.elObject = DefaultEL.create(root);
        this.isStrict = isStrict;
    }

    /**
     * 计算
     *
     * @param el tag name
     * @return
     */
    @Override
    public Object compute(String el) {
        try {
            return elObject.eval(el);
        } catch (ExpressionEvalException e) {
            if (isStrict) {
                throw e;
            }
            // Cannot calculate the expression, the default returns null
            return null;
        }
    }
}