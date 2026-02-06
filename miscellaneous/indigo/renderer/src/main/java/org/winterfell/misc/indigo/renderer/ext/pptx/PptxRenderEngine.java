package org.winterfell.misc.indigo.renderer.ext.pptx;

import com.google.auto.service.AutoService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterfell.misc.indigo.renderer.RenderEngine;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsMetaTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 渲染 .pptx
 * </p>
 *
 * @author alex
 * @version v1.0 2022/4/19
 */
@AutoService(RenderEngine.class)
public class PptxRenderEngine implements RenderEngine {

    private static final Logger logger = LoggerFactory.getLogger(PptxRenderEngine.class);

    /**
     * 是否支持某文件类型
     * 仅支持 .pptx 格式
     *
     * @param fileExt eg txt
     * @return true/false
     */
    @Override
    public boolean supportsFileType(String fileExt) {
        return ".pptx".contentEquals(fileExt);
    }

    /**
     * init engine
     *
     * @param config 引擎个性化配置
     * @return self
     */
    @Override
    public void init(Map<String, ?> config) {
        // do nothing
    }

    /**
     * 渲染文档
     *
     * @param tplFile 模板文件
     * @param tplData 渲染数据
     * @return byte[]
     */
    @Override
    public <T> byte[] render(File tplFile, Map<String, T> tplData) {
        return toBytes(SlideShowTemplate.compile(tplFile).render(tplData));
    }

    /**
     * 渲染文档
     *
     * @param tplStream 模板stream
     * @param tplData   渲染数据
     * @return byte[]
     */
    @Override
    public <T> byte[] render(InputStream tplStream, Map<String, T> tplData) {
        return toBytes(SlideShowTemplate.compile(tplStream).render(tplData));
    }

    @Override
    public void write(OutputStream stream) {
        // TODO

    }

    /**
     * 列出模板中的标记
     * {{tagName}}
     *
     * @param tplFile 模板文件
     * @return <tagName>
     */
    @Override
    public List<String> tagNames(File tplFile) {
        try {
            return tagNames(new FileInputStream(tplFile));
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
        return new ArrayList<>(1);
    }

    /**
     * tag names
     *
     * @param tplStream 模板文件流
     * @return list
     */
    @Override
    public List<String> tagNames(InputStream tplStream) {
        List<SsMetaTemplate> eleTemplates = SlideShowTemplate.compile(tplStream).getEleTemplates();
        return eleTemplates.stream().map(SsMetaTemplate::variable).collect(Collectors.toList());
    }


    private byte[] toBytes(@NonNull SlideShowTemplate template) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(1024)) {
            template.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return new byte[0];
    }
}