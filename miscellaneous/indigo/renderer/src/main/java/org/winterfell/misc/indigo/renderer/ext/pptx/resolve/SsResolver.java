package org.winterfell.misc.indigo.renderer.ext.pptx.resolve;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsMetaTemplate;

import java.util.List;

/**
 * 解析接口
 * @author alex
 * @version v1.0 2020/11/17
 */
public interface SsResolver {

    /**
     * 解析变量模板等
     * @param slideShow  slideshow
     * @return
     */
    List<SsMetaTemplate> resolve(XMLSlideShow slideShow);

}