package org.winterfell.misc.hutool.mini.io;

import org.winterfell.misc.hutool.mini.AssertUtil;
import org.winterfell.misc.hutool.mini.ClassUtil;
import org.winterfell.misc.hutool.mini.StringUtil;
import org.winterfell.misc.hutool.mini.URLUtil;

import java.net.URL;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/8
 */
public class ClassPathResource extends UrlResource {

    private String path;
    private ClassLoader classLoader;
    private Class<?> clazz;

    // -------------------------------------------------------------------------------------- Constructor start
    /**
     * 构造
     *
     * @param path 相对于ClassPath的路径
     */
    public ClassPathResource(String path) {
        this(path, null, null);
    }

    /**
     * 构造
     *
     * @param path 相对于ClassPath的路径
     * @param classLoader {@link ClassLoader}
     */
    public ClassPathResource(String path, ClassLoader classLoader) {
        this(path, classLoader, null);
    }

    /**
     * 构造
     *
     * @param path 相对于给定Class的路径
     * @param clazz {@link Class} 用于定位路径
     */
    public ClassPathResource(String path, Class<?> clazz) {
        this(path, null, clazz);
    }

    /**
     * 构造
     *
     * @param pathBaseClassLoader 相对路径
     * @param classLoader {@link ClassLoader}
     * @param clazz {@link Class} 用于定位路径
     */
    public ClassPathResource(String pathBaseClassLoader, ClassLoader classLoader, Class<?> clazz) {
        super((URL) null);
        AssertUtil.notNull(pathBaseClassLoader, "Path must not be null");
        this.path = normalizePath(pathBaseClassLoader);
        this.classLoader = (classLoader != null) ? classLoader : ClassUtil.getClassLoader();
        this.clazz = clazz;
        this.name = FileUtil.file(this.path).getName();
        initUrl();
    }
    // -------------------------------------------------------------------------------------- Constructor end

    /**
     * 获得Path
     *
     * @return path
     */
    public final String getPath() {
        return this.path;
    }

    /**
     * 获得绝对路径Path<br>
     * 对于不存在的资源，返回拼接后的绝对路径
     *
     * @return 绝对路径path
     */
    public final String getAbsolutePath() {
        if (FileUtil.isAbsolutePath(this.path)) {
            return this.path;
        }
        // url在初始化的时候已经断言，此处始终不为null
        return FileUtil.normalize(URLUtil.getDecodedPath(this.url));
    }

    /**
     * 获得 {@link ClassLoader}
     *
     * @return {@link ClassLoader}
     */
    public final ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /**
     * 根据给定资源初始化URL
     */
    private void initUrl() {
        if (null != this.clazz) {
            super.url = this.clazz.getResource(this.path);
        } else if (null != this.classLoader) {
            super.url = this.classLoader.getResource(this.path);
        } else {
            super.url = ClassLoader.getSystemResource(this.path);
        }
        if (null == super.url) {
            throw new NoResourceException("Resource of path [{}] not exist!", this.path);
        }
    }

    @Override
    public String toString() {
        return (null == this.path) ? super.toString() : "classpath:" + this.path;
    }

    /**
     * 标准化Path格式
     *
     * @param path Path
     * @return 标准化后的path
     */
    private String normalizePath(String path) {
        // 标准化路径
        path = FileUtil.normalize(path);
        path = StringUtil.removePrefix(path, StringUtil.SLASH);

        AssertUtil.isFalse(FileUtil.isAbsolutePath(path), "Path [{}] must be a relative path !", path);
        return path;
    }
}
