package org.winterfell.shared.as.openapi.knife4j;

import org.winterfell.shared.as.openapi.OpenApiProperties;
import org.winterfell.shared.as.openapi.knife4j.util.MarkdownUtils;
import com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownChildren;
import com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownFile;
import com.github.xiaoymin.knife4j.core.model.MarkdownProperty;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import com.github.xiaoymin.knife4j.core.util.CommonUtils;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/9/25
 */
@Slf4j
public class OpenApiExtensionResolver {

    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    private final Map<String, List<OpenApiExtendMarkdownFile>> markdownFileMaps = new HashMap<>();
    /**
     * 个性化配置
     */
    private final OpenApiProperties.DocUiSetting setting;

    /**
     * 分组文档集合
     */
    private final List<MarkdownProperty> markdownProperties;

    public List<OpenApiExtendMarkdownFile> getMarkdownFiles() {
        if (CollectionUtils.isNotEmpty(markdownFileMaps)) {
            List<OpenApiExtendMarkdownFile> markdownFiles = new LinkedList<>();
            for (Map.Entry<String, List<OpenApiExtendMarkdownFile>> entry : this.markdownFileMaps.entrySet()) {
                if (CollectionUtils.isNotEmpty(entry.getValue())) {
                    markdownFiles.addAll(entry.getValue());
                }
            }
            return markdownFiles;

        }
        return Collections.EMPTY_LIST;
    }

    public void start() {
        if (log.isDebugEnabled()) {
            log.debug("Resolver method start...");
        }
        // 初始化其他文档
        // 其他文档是否为空
        if (CollectionUtils.isNotEmpty(this.markdownProperties)) {
            for (MarkdownProperty markdownProperty : this.markdownProperties) {
                if (StrUtil.isNotBlank(markdownProperty.getName()) && StrUtil.isNotBlank(markdownProperty.getLocations())) {
                    String swaggerGroupName = StrUtil.isNotBlank(markdownProperty.getGroup()) ? markdownProperty.getGroup() : "default";
                    OpenApiExtendMarkdownFile openApiExtendMarkdownFile = new OpenApiExtendMarkdownFile();
                    openApiExtendMarkdownFile.setName(markdownProperty.getName());
                    openApiExtendMarkdownFile.setGroup(swaggerGroupName);
                    List<OpenApiExtendMarkdownChildren> allChildrenLists = new ArrayList<>();
                    // 多个location以分号(;)进行分隔
                    String[] locations = markdownProperty.getLocations().split(";");
                    if (!CollectionUtils.isEmpty(locations)) {
                        for (String location : locations) {
                            if (StrUtil.isNotBlank(location)) {
                                List<OpenApiExtendMarkdownChildren> childrenList = readLocations(location);
                                if (CollectionUtils.isNotEmpty(childrenList)) {
                                    allChildrenLists.addAll(childrenList);
                                }

                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(allChildrenLists)) {
                        openApiExtendMarkdownFile.setChildren(allChildrenLists);
                    }
                    // 判断是否存在
                    if (markdownFileMaps.containsKey(swaggerGroupName)) {
                        markdownFileMaps.get(swaggerGroupName).add(openApiExtendMarkdownFile);
                    } else {
                        markdownFileMaps.put(swaggerGroupName, CollectionUtils.newArrayList(openApiExtendMarkdownFile));
                    }
                }
            }
        }
        // 判断主页文档
        if (this.setting != null) {
            if (this.setting.isEnableHomeCustom()) {
                if (StrUtil.isNotBlank(this.setting.getHomeCustomPath())) {
                    String content = readCustomHome(this.setting.getHomeCustomPath());
                    // 赋值
                    this.setting.setHomeCustomLocation(content);
                }
            }
        }
    }

    /**
     * 读取自定义主页markdown的内容
     *
     * @param customHomeLocation 路径
     * @return markdown内容
     */
    private String readCustomHome(String customHomeLocation) {
        String customHomeContent = "";
        try {
            Resource[] resources = resourceResolver.getResources(customHomeLocation);
            if (resources.length > 0) {
                // 取第1个
                Resource resource = resources[0];
                customHomeContent = new String(CommonUtils.readBytes(resource.getInputStream()), "UTF-8");
            }
        } catch (Exception e) {
            log.warn("(Ignores) Failed to read CustomeHomeLocation Markdown files,Error Message:{} ", e.getMessage());
        }
        return customHomeContent;
    }

    /**
     * 根据路径读取markdown文件
     *
     * @param locations markdown文件路径
     * @return 文档集合
     */
    private List<OpenApiExtendMarkdownChildren> readLocations(String locations) {
        try {
            List<OpenApiExtendMarkdownChildren> openApiExtendMarkdownChildrenList = new ArrayList<>();
            Resource[] resources = resourceResolver.getResources(locations);
            if (resources.length > 0) {
                for (Resource resource : resources) {
                    OpenApiExtendMarkdownChildren markdownFile = readMarkdownChildren(resource);
                    if (markdownFile != null) {
                        openApiExtendMarkdownChildrenList.add(markdownFile);
                    }
                }
                return openApiExtendMarkdownChildrenList;
            }
        } catch (Exception e) {
            log.warn("(Ignores) Failed to read Markdown files,Error Message:{} ", e.getMessage());
        }
        return null;
    }

    private OpenApiExtendMarkdownChildren readMarkdownChildren(Resource resource) {
        return MarkdownUtils.resolveMarkdownResource(resource);
    }

    public OpenApiExtensionResolver(OpenApiProperties.DocUiSetting setting, List<MarkdownProperty> markdownProperties) {
        this.setting = setting;
        this.markdownProperties = markdownProperties;
    }
}
