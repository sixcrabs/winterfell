package org.winterfell.misc.indigo.renderer.ext.pptx;

import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 封装原生的 {@link XMLSlideShow}
 *
 * @author alex
 * @version v1.0 2020/11/17
 */
public class NiceSlideShow extends XMLSlideShow {

    public NiceSlideShow() {
        super();
    }

    public NiceSlideShow(InputStream is) throws IOException {
        super(is);
        slideshowRead();
    }

    /**
     * TODO
     */
    private void slideshowRead() {

        initAllElement(this);

    }

    private void initAllElement(SlideShow body) {
       List<XSLFSlide> slides =  body.getSlides();
       slides.stream().map(slide->slide.getShapes());
//        readParagraphs(body.getParagraphs());
//        readTables(body.getTables());
    }
}