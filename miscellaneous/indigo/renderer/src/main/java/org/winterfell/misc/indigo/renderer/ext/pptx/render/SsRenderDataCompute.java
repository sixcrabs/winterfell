package org.winterfell.misc.indigo.renderer.ext.pptx.render;

/**
 * 用于从数据模型中获取到对应tag的数据值
 *
 * @author alex
 * @version v1.0 2020/11/18
 */
public interface SsRenderDataCompute {

    /**
     * 计算
     * @param el  tag name
     * @return
     */
    Object compute(String el);
}