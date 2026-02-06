package org.winterfell.misc.indigo.renderer.ext.plaintext;

import com.google.auto.service.AutoService;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.MustacheException;
import com.samskivert.mustache.MustacheResolver;
import org.mozilla.universalchardet.UniversalDetector;
import org.winterfell.misc.hutool.mini.io.IoUtil;
import org.winterfell.misc.indigo.renderer.RenderEngine;
import org.winterfell.misc.indigo.renderer.support.IndigoRenderException;
import org.winterfell.misc.indigo.renderer.support.ValueWrapper;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/4/19
 */
@AutoService(RenderEngine.class)
public class MustacheRenderEngine implements RenderEngine {

    private Mustache.Compiler compiler;

    /**
     * 是否支持某文件类型
     *
     * @param fileExt eg txt
     * @return
     */
    @Override
    public boolean supportsFileType(String fileExt) {
        return fileExt.endsWith(".txt") || fileExt.endsWith(".mustache");
    }

    /**
     * init engine
     */
    @Override
    public void init(Map<String, ?> config) {
        compiler = Mustache.compiler();
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
        try (InputStream stream = Files.newInputStream(tplFile.toPath())) {
            String tplString = IoUtil.read(stream, Charset.forName(UniversalDetector.detectCharset(tplFile)));
            String retString = render(tplString, tplData);
            if (!retString.isEmpty()) {
                return retString.getBytes(StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new IndigoRenderException(e);
        }
        return new byte[0];
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
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compiler.compile(new InputStreamReader(tplStream, StandardCharsets.UTF_8))
                    .execute(tplData, new OutputStreamWriter(baos, StandardCharsets.UTF_8));
            if (baos.size() > 0) {
                return baos.toByteArray();
            }
        } catch (MustacheException e) {
            throw new IndigoRenderException(e);
        }
        return new byte[0];
    }


    @Override
    public void write(OutputStream stream) {
        // TODO

    }

    /**
     * rende string
     *
     * @param tplString
     * @param tplData
     * @return
     */
    public String render(String tplString, Map tplData) {
        StringWriter writer = new StringWriter(tplString.length());
        compiler.compile(tplString).execute(tplData, writer);
        writer.flush();
        return writer.toString();
    }

    /**
     * {{tagName}}
     *
     * @param tplFile
     * @return tagName
     */
    @Override
    public List<String> tagNames(File tplFile) {
        try (InputStream stream = Files.newInputStream(tplFile.toPath())) {
            String content = IoUtil.read(stream, Charset.forName(UniversalDetector.detectCharset(tplFile)));
            return MustacheResolver.resolveVariableNames(compiler, content);
        } catch (IOException e) {
            throw new IndigoRenderException(e.getLocalizedMessage());
        }
    }

    /**
     * tag names
     *
     * @param tplStream
     * @return
     */
    @Override
    public List<String> tagNames(InputStream tplStream) {
        String tplString = new BufferedReader(new InputStreamReader(tplStream, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining(System.lineSeparator()));
        return tagNames(tplString);
    }

    public List<String> tagNames(String tplString) {
        return MustacheResolver.resolveVariableNames(compiler, tplString);
    }

}