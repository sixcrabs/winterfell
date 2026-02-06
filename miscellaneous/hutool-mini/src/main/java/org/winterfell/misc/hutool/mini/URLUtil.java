package org.winterfell.misc.hutool.mini;

import org.winterfell.misc.hutool.mini.io.IORuntimeException;
import org.winterfell.misc.hutool.mini.io.IoUtil;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.jar.JarFile;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/8
 */
public class URLUtil {

    /** 针对ClassPath路径的伪协议前缀（兼容Spring）: "classpath:" */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    /** URL 前缀表示文件: "file:" */
    public static final String FILE_URL_PREFIX = "file:";
    /** URL 前缀表示jar: "jar:" */
    public static final String JAR_URL_PREFIX = "jar:";
    /** URL 前缀表示war: "war:" */
    public static final String WAR_URL_PREFIX = "war:";
    /** URL 协议表示文件: "file" */
    public static final String URL_PROTOCOL_FILE = "file";
    /** URL 协议表示Jar文件: "jar" */
    public static final String URL_PROTOCOL_JAR = "jar";
    /** URL 协议表示zip文件: "zip" */
    public static final String URL_PROTOCOL_ZIP = "zip";
    /** URL 协议表示WebSphere文件: "wsjar" */
    public static final String URL_PROTOCOL_WSJAR = "wsjar";
    /** URL 协议表示JBoss zip文件: "vfszip" */
    public static final String URL_PROTOCOL_VFSZIP = "vfszip";
    /** URL 协议表示JBoss文件: "vfsfile" */
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
    /** URL 协议表示JBoss VFS资源: "vfs" */
    public static final String URL_PROTOCOL_VFS = "vfs";
    /** Jar路径以及内部文件路径的分界符: "!/" */
    public static final String JAR_URL_SEPARATOR = "!/";
    /** WAR路径及内部文件路径分界符 */
    public static final String WAR_URL_SEPARATOR = "*/";


    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param file URL对应的文件对象
     * @return URL
     * @exception UtilException MalformedURLException
     */
    public static URL getURL(File file) {
        AssertUtil.notNull(file, "File is null !");
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new UtilException(e, "Error occured when get URL!");
        }
    }

    /**
     * 从URL中获取流
     * @param url {@link URL}
     * @return InputStream流
     * @since 3.2.1
     */
    public static InputStream getStream(URL url) {
        AssertUtil.notNull(url);
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }


    /**
     * 获得Reader
     *
     * @param url {@link URL}
     * @param charset 编码
     * @return {@link BufferedReader}
     * @since 3.2.1
     */
    public static BufferedReader getReader(URL url, Charset charset){
        return IoUtil.getReader(getStream(url), charset);
    }

    /**
     * 转URL为URI
     *
     * @param url URL
     * @return URI
     * @exception UtilException 包装URISyntaxException
     */
    public static URI toURI(URL url) throws UtilException {
        if (null == url) {
            return null;
        }
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new UtilException(e);
        }
    }

    /**
     * 从URL对象中获取不被编码的路径Path<br>
     * 对于本地路径，URL对象的getPath方法对于包含中文或空格时会被编码，导致本读路径读取错误。<br>
     * 此方法将URL转为URI后获取路径用于解决路径被编码的问题
     *
     * @param url {@link URL}
     * @return 路径
     * @since 3.0.8
     */
    public static String getDecodedPath(URL url) {
        String path = null;
        try {
            // URL对象的getPath方法对于包含中文或空格的问题
            path = URLUtil.toURI(url).getPath();
        } catch (UtilException e) {
            // ignore
        }
        return (null != path) ? path : url.getPath();
    }

    /**
     * 解码URL<br>
     * 将%开头的16进制表示的内容解码。
     *
     * @param url URL
     * @param charset 编码
     * @return 解码后的URL
     * @exception UtilException UnsupportedEncodingException
     */
    public static String decode(String url, String charset) throws UtilException{
        try {
            return URLDecoder.decode(url, charset);
        } catch (UnsupportedEncodingException e) {
            throw new UtilException(e);
        }
    }

    /**
     * 从URL中获取JarFile
     *
     * @param url URL
     * @return JarFile
     * @since 4.1.5
     */
    public static JarFile getJarFile(URL url) {
        try {
            JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
            return urlConnection.getJarFile();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
