package org.winterfell.shared.as.openapi.knife4j.util;

import com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownChildren;
import com.github.xiaoymin.knife4j.core.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/9/25
 */
@Slf4j
public final class MarkdownUtils {

    private MarkdownUtils() {
    }


    /**
     * Resolve markdown files
     * @param resource markdown file
     * @return OpenApiExtendMarkdownChildren
     */
    public static OpenApiExtendMarkdownChildren resolveMarkdownResource(Resource resource) {
        try {
            if (resource != null) {
                OpenApiExtendMarkdownChildren markdownFile = new OpenApiExtendMarkdownChildren();
                if (log.isDebugEnabled()) {
                    log.debug("read file:" + resource.getFilename());
                }
                // 只读取md
                if (Objects.toString(resource.getFilename(), "").toLowerCase().endsWith(".md")) {
                    // if (".md".equals(Objects.toString(resource.getFilename(),""))){
                    try {
                        String title = CommonUtils.resolveMarkdownTitle(resource.getInputStream(), resource.getFilename());
                        markdownFile.setTitle(title);
                        markdownFile.setContent(new String(CommonUtils.readBytes(resource.getInputStream()), StandardCharsets.UTF_8));
                        return markdownFile;
                    } catch (Exception e) {
                        log.warn("(Ignores) Failed to read Markdown files,Error Message:{} ", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("(Ignores) Failed to read Markdown files,Error Message:{} ", e.getMessage());
        }
        return null;
    }
}
