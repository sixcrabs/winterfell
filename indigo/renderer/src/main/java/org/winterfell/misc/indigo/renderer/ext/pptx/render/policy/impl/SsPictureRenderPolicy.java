package org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.impl;

import com.google.auto.service.AutoService;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.*;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.RenderContext;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.data.SsPictureRenderData;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.AbstractSsRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.SsRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsElementTemplate;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.impl.SsPictureTemplate;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.impl.SsRunTemplate;

/**
 * 渲染图片
 *
 * @author alex
 * @version v1.0 2020/11/18
 */
@AutoService(SsRenderPolicy.class)
public class SsPictureRenderPolicy extends AbstractSsRenderPolicy<SsPictureRenderData> {

    /**
     * 实现渲染
     * - 插入一个图片
     * - 替换占位符图片
     *
     * @param context
     * @throws Exception
     */
    @Override
    public void doRender(RenderContext<SsPictureRenderData> context) throws Exception {
        SsElementTemplate elementTemplate = context.getElementTemplate();
        SsPictureRenderData pictureRenderData = context.getData();
        logger.info("render picture template...");
        if (elementTemplate instanceof SsPictureTemplate) {
            SsPictureTemplate tpl = (SsPictureTemplate) elementTemplate;
            XSLFPictureData pictureData = tpl.getPictureData();
            pictureData.setData(pictureRenderData.getImage());
        } else {
            SsRunTemplate tpl = (SsRunTemplate) elementTemplate;
            XSLFShape shape = tpl.getShape();
            if (shape != null) {
                final PictureData.PictureType pictureType = PictureData.PictureType.valueOf(pictureRenderData.getPictureType().name());
                XSLFShapeContainer shapeContainer = shape.getParent();
                XMLSlideShow slideShow;
                if (shapeContainer instanceof XSLFSheet) {
                    XSLFSheet sheet = (XSLFSheet) shapeContainer;
                    slideShow = sheet.getSlideShow();
                } else if (shapeContainer instanceof XSLFGroupShape) {
                    XSLFGroupShape groupShape = (XSLFGroupShape) shapeContainer;
                    slideShow = groupShape.getSheet().getSlideShow();
                } else {
                    logger.error("render picture template error: shape container must be `XSLFSheet` or `XSLFGroupShape`");
                    return;
                }
                if (slideShow != null) {
                    XSLFPictureData pictureData = slideShow.addPicture(pictureRenderData.getImage(), pictureType);
                    // clear
                    shapeContainer.clear();
                    shapeContainer.createPicture(pictureData);
                }
            }
        }
        logger.info("render picture template successfully");
    }

    /**
     * 数据验证
     *
     * @param data
     * @return
     */
    @Override
    protected boolean validate(SsPictureRenderData data) {
        if (data == null) {
            return false;
        }
        if (data.getImage().length == 0) {
            logger.error("图片类型数据不能为空");
            return false;
        }
        return true;
    }
}