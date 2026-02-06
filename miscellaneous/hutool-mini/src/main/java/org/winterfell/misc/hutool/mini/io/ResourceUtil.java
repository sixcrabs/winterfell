package org.winterfell.misc.hutool.mini.io;

import org.winterfell.misc.hutool.mini.ClassLoaderUtil;
import org.winterfell.misc.hutool.mini.ClassUtil;
import org.winterfell.misc.hutool.mini.collection.EnumerationIter;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/8
 */
public final class ResourceUtil {


    /**
     * 获得资源的URL<br>
     * 路径用/分隔，例如:
     *
     * <pre>
     * config/a/db.config
     * spring/xml/test.xml
     * </pre>
     *
     * @param resource 资源（相对Classpath的路径）
     * @return 资源URL
     */
    public static URL getResource(String resource) throws IORuntimeException {
        return getResource(resource, null);
    }

    /**
     * 获得资源相对路径对应的URL
     *
     * @param resource 资源相对路径
     * @param baseClass 基准Class，获得的相对路径相对于此Class所在路径，如果为{@code null}则相对ClassPath
     * @return {@link URL}
     */
    public static URL getResource(String resource, Class<?> baseClass) {
        return (null != baseClass) ? baseClass.getResource(resource) : ClassUtil.getClassLoader().getResource(resource);
    }

    /**
     * 获取{@link Resource} 资源对象<br>
     * 如果提供路径为绝对路径，返回{@link FileResource}，否则返回{@link ClassPathResource}
     *
     * @param path 路径，可以是绝对路径，也可以是相对路径
     * @return {@link Resource} 资源对象
     */
    public static Resource getResourceObj(String path) {
        return FileUtil.isAbsolutePath(path) ? new FileResource(path) : new ClassPathResource(path);
    }

    /**
     * 获取指定路径下的资源Iterator<br>
     * 路径格式必须为目录格式,用/分隔，例如:
     *
     * <pre>
     * config/a
     * spring/xml
     * </pre>
     *
     * @param resource 资源路径
     * @return 资源列表
     * @since 4.1.5
     */
    public static EnumerationIter<URL> getResourceIter(String resource) {
        final Enumeration<URL> resources;
        try {
            resources = ClassLoaderUtil.getClassLoader().getResources(resource);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return new EnumerationIter<>(resources);
    }

}
