package org.winterfell.misc.indigo.renderer.ext.pptx;

import org.apache.poi.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterfell.misc.indigo.renderer.ext.pptx.ex.SsResolverException;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.DefaultSsRenderer;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.SsRenderer;
import org.winterfell.misc.indigo.renderer.ext.pptx.resolve.DefaultSsResolver;
import org.winterfell.misc.indigo.renderer.ext.pptx.resolve.SsResolver;
import org.winterfell.misc.indigo.renderer.ext.pptx.support.CloseIOUtils;
import org.winterfell.misc.indigo.renderer.ext.pptx.support.Preconditions;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsMetaTemplate;

import java.io.*;
import java.util.List;

/**
 * Template of .pptx
 *
 * @author alex
 * @version v1.0 2020/11/17
 */
public class SlideShowTemplate implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(SlideShowTemplate.class);

    private static final String SUPPORT_MINIMUM_VERSION = "4.0.0";

    private NiceSlideShow slideShow;

    private SsResolver resolver;

    private SsRenderer renderer;

    private List<SsMetaTemplate> eleTemplates;


    static {
        try {
            Class.forName("org.apache.poi.Version");
            Preconditions.checkMinimumVersion(Version.getVersion(), SUPPORT_MINIMUM_VERSION,
                    (cur, min) -> "Require Apach POI version at least " + min + ", but now is " + cur
                            + ", please check the dependency of project.");
        } catch (ClassNotFoundException e) {
            logger.error("Cannot find the class [org.apache.poi.Version]");
        }

    }

    private SlideShowTemplate() {
    }

    public static SlideShowTemplate compile(String filePath) {
        return compile(new File(filePath));
    }

    public static SlideShowTemplate compile(File file) {
        try {
            return compile(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new SsResolverException("Cannot find the file [" + file.getPath() + "]", e);
        }
    }

    /**
     * compile stream
     * @param stream stream of template file
     * @return
     */
    public static SlideShowTemplate compile(InputStream stream) {
        try {
            SlideShowTemplate template = new SlideShowTemplate();
            template.slideShow = new NiceSlideShow(stream);
            template.resolver = new DefaultSsResolver();
            template.renderer = new DefaultSsRenderer();
            template.eleTemplates = template.resolver.resolve(template.slideShow);
            return template;
        } catch (IOException e) {
            logger.error("compile error: {}", e.getLocalizedMessage());
            throw new SsResolverException("Compile template failed", e);
        }
    }


    /**
     * Render the template by data model
     *
     * @param model
     * @return
     */
    public SlideShowTemplate render(Object model) {
        this.renderer.render(this, model);
        return this;
    }

    /**
     * write to output stream, do'not forget invoke
     * #{@link SlideShowTemplate#close()}, #{@link OutputStream#close()} finally
     *
     * @param out eg.ServletOutputStream
     * @throws IOException
     */
    public void write(OutputStream out) throws IOException {
        this.slideShow.write(out);
    }

    /**
     * write to file
     *
     * @param path
     * @throws IOException
     */
    public void writeToFile(String path) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            this.write(out);
            out.flush();
        } finally {
            CloseIOUtils.closeQuietlyMulti(this.slideShow, out);
        }
    }

    /**
     * reload the template
     *
     * @param slideShow
     */
    public void reload(NiceSlideShow slideShow) {
        CloseIOUtils.closeLoggerQuietly(this.slideShow);
        this.slideShow = slideShow;
        this.eleTemplates = this.resolver.resolve(slideShow);
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        this.slideShow.close();
    }

    public NiceSlideShow getSlideShow() {
        return slideShow;
    }

    public SsResolver getResolver() {
        return resolver;
    }

    public SsRenderer getRenderer() {
        return renderer;
    }

    public List<SsMetaTemplate> getEleTemplates() {
        return eleTemplates;
    }
}