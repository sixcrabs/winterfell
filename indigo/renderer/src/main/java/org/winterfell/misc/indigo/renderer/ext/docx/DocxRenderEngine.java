package org.winterfell.misc.indigo.renderer.ext.docx;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.template.MetaTemplate;
import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterfell.misc.hutool.mini.MapUtil;
import org.winterfell.misc.indigo.renderer.RenderEngine;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import static org.winterfell.misc.indigo.renderer.support.DocxRenderUtil.process;


/**
 * <p>
 * docx 渲染引擎实现
 * </p>
 *
 * @author alex
 * @version v1.0 2022/4/19
 */
@AutoService(RenderEngine.class)
public class DocxRenderEngine implements RenderEngine {

    private Configure renderConfig = Configure.createDefault();

    private static final Logger logger = LoggerFactory.getLogger(DocxRenderEngine.class);

    private XWPFTemplate template = null;

    /**
     * 支持的文件类型 eg docx / txt / xls
     *
     * @param fileExt
     * @return
     */
    @Override
    public boolean supportsFileType(String fileExt) {
        return ".docx".contentEquals(fileExt);
    }

    @Override
    public void init(Map config) {
        // SPI 方式加载所有的自定义渲染
        ConfigureBuilder configureBuilder = Configure.builder();
//        configureBuilder.addPlugin("~".charAt(0), new LoopRowTableRenderPolicy());
        ServiceLoader<DocxCustomRenderPolicy> policies = ServiceLoader.load(DocxCustomRenderPolicy.class);
        try {
            for (DocxCustomRenderPolicy policy : policies) {
                DocxCustomRenderPolicy instance = policy.getClass().newInstance();
                if (policy.renderTag() != null) {
                    configureBuilder.bind(policy.renderTag(), instance);
                } else if (policy.renderPluginChar() != null) {
                    // 注册为 插件
                    configureBuilder.addPlugin(policy.renderPluginChar(), instance);
                } else {
                    // error
                    logger.error("policy of [{}] must have `render tag` or `plugin char` ", policy.getClass().getSimpleName());
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.getLocalizedMessage());
        }
        String mode = MapUtil.getStr(config, "mode");
        if ("spel".equalsIgnoreCase(mode)) {
            // 设置为springEl 表达式模式
            configureBuilder.useSpringEL(true);
        }
        renderConfig = configureBuilder.build();
    }

    /**
     * {{tagName}}
     *
     * @param tplFile
     * @return tagName
     */
    @Override
    public List<String> tagNames(File tplFile) {
        try {
            return tagNames(new FileInputStream(tplFile));
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
        return Lists.newArrayList();
    }

    /**
     * tag names
     *
     * @param tplStream
     * @return
     */
    @Override
    public List<String> tagNames(InputStream tplStream) {
        List<MetaTemplate> elementTemplates = XWPFTemplate.compile(tplStream).getElementTemplates();
        if (elementTemplates != null && !elementTemplates.isEmpty()) {
            return elementTemplates.stream().map(MetaTemplate::variable).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    /**
     * 渲染文档
     *
     * @param tplStream 模板stream
     * @param tplData   渲染数据
     * @return
     */
    @Override
    public byte[] render(InputStream tplStream, Map tplData) {
        template = XWPFTemplate.compile(tplStream, renderConfig).render(process(tplData));
        return toBytes(template);
    }

    /**
     * 渲染文档
     *
     * @param tplFile 模板文件
     * @param tplData 渲染数据
     * @return
     */
    @Override
    public byte[] render(File tplFile, Map tplData) {
        template = XWPFTemplate.compile(tplFile, renderConfig).render(process(tplData));
        return toBytes(template);
    }

    @Override
    public void write(OutputStream stream) {
        if (template != null && stream != null) {
            try {
                template.writeAndClose(stream);
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
    }

    private byte[] toBytes(@NonNull XWPFTemplate template) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(1024)) {
            template.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return new byte[0];
    }
}