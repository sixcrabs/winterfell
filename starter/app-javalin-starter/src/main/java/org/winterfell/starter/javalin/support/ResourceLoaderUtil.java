package org.winterfell.starter.javalin.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterfell.misc.hutool.mini.io.*;

import java.util.*;

/**
 * <p>
 * 资源加载 util
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/26
 */
public final class ResourceLoaderUtil {

    private ResourceLoaderUtil() {
    }

    private static final Logger log = LoggerFactory.getLogger(ResourceLoaderUtil.class);

    /**
     * 扫描资源文件路径
     */
    private static final List<String> SCAN_LOCATIONS = Collections.unmodifiableList(Arrays.asList("file:cfg/", "file:./config/", "file:./"
            , "config/", ""));

    /**
     * load resource of data.json
     *
     * @return
     */
    public static Resource loadResource(String resourcePath) {
        Resource resource = null;
        for (String loc : SCAN_LOCATIONS) {
            try {
                resource = ResourceUtil.getResourceObj(loc.concat(resourcePath));
                String path = resource.getUrl().getPath();
                // 判定给定的路径是否为Jar
                int index = path.lastIndexOf(FileUtil.JAR_PATH_EXT);
                if (index != -1) {
                    // 更新路径
                    String[] strs = path.replaceFirst(FileUtil.PATH_FILE_PRE, "").split("/");
                    List<String> nPaths = new ArrayList<>(strs.length);
                    for (int i = 0; i < strs.length; i++) {
                        if (!strs[i].contains(FileUtil.JAR_PATH_EXT)) {
                            nPaths.add(strs[i]);
                        }
                    }
                    // 开始尝试 /app/
                    String filePath = String.join("/", nPaths);
                    FileResource fileResource = new FileResource(filePath);
                    if (fileResource.getFile().exists()) {
                        resource = fileResource;
                        break;
                    } else {
                        // 尝试/cfg 下
                        fileResource = new FileResource(filePath.replaceFirst("/app/", "/cfg/"));
                        if (fileResource.getFile().exists()) {
                            resource = fileResource;
                            break;
                        } else {
                            // 尝试 / 根目录下
                            fileResource = new FileResource(filePath.replaceFirst("/app/", "/"));
                            if (fileResource.getFile().exists()) {
                                resource = fileResource;
                                break;
                            }
                        }
                    }
                }
                if (ClassPathResource.class.isAssignableFrom(resource.getClass())) {
                    if (((ClassPathResource) resource).getFile().exists()) {
                        break;
                    }
                } else {
                    if (((FileResource) resource).getFile().exists()) {
                        break;
                    }
                }
            } catch (Exception e) {
                log.warn(e.getLocalizedMessage());
                resource = null;
            }
        }
        if (Objects.isNull(resource)) {
            log.warn(" cannot load resource [{}]", resourcePath);
            return null;
        }
        return resource;
    }
}
