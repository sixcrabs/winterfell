package org.winterfell.misc.indigo.renderer;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * render engine
 * </p>
 *
 * @author alex
 * @version v1.0 2022/4/19
 */
public interface RenderEngine {

    /**
     * 是否支持某文件类型
     *
     * @param fileExt eg txt
     * @return true/false
     */
    boolean supportsFileType(String fileExt);

    /**
     * init engine
     *
     * @param config 引擎个性化配置
     * @return self
     */
    void init(Map<String, ?> config);

    /**
     * 渲染文档
     *
     * @param tplFile 模板文件
     * @param tplData 渲染数据
     * @return byte[]
     */
    <T> byte[] render(File tplFile, Map<String, T> tplData);

    /**
     * 渲染文档
     *
     * @param tplStream 模板stream
     * @param tplData   渲染数据
     * @return byte[]
     */
    <T> byte[] render(InputStream tplStream, Map<String, T> tplData);

    /**
     * 写入流
     *
     * @param stream
     */
    void write(OutputStream stream);

    /**
     * 列出模板中的标记
     * {{tagName}}
     *
     * @param tplFile 模板文件
     * @return <tagName>
     */
    List<String> tagNames(File tplFile);

    /**
     * tag names
     *
     * @param tplStream 模板文件流
     * @return list
     */
    List<String> tagNames(InputStream tplStream);

}