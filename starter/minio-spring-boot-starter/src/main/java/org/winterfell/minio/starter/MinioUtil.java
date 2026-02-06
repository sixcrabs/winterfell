package org.winterfell.minio.starter;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/4/24
 */
public final class MinioUtil {

    public static final String DOT = ".";

    public static final String EMPTY = "";

    /**
     * 类Unix路径分隔符
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * Windows路径分隔符
     */
    private static final char WINDOWS_SEPARATOR = '\\';
    /**
     * Windows下文件名中的无效字符
     */
    private static Pattern FILE_NAME_INVALID_PATTERN_WIN = Pattern.compile("[\\\\/:*?\"<>|]");

    /**
     * 将byte[]写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param content    写入的内容
     * @throws IORuntimeException IO异常
     */
    public static void write(OutputStream out, boolean isCloseOut, byte[] content) throws IOException {
        try {
            out.write(content);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (isCloseOut) {
                close(out);
            }
        }
    }

    /**
     * 关闭<br>
     * 关闭失败不会抛出异常
     *
     * @param closeable 被关闭的对象
     */
    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
            }
        }
    }

    /**
     * 获取文件扩展名，扩展名不带“.”
     *
     * @param file 文件
     * @return 扩展名
     */
    public static String extName(File file) {
        if (null == file) {
            return null;
        }
        if (file.isDirectory()) {
            return null;
        }
        return extName(file.getName());
    }

    /**
     * 获得文件的扩展名，扩展名不带“.”
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String extName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int index = fileName.lastIndexOf(DOT);
        if (index == -1) {
            return EMPTY;
        } else {
            String ext = fileName.substring(index + 1);
            // 扩展名中不能包含路径相关的符号
            return (ext.contains(String.valueOf(UNIX_SEPARATOR)) || ext.contains(String.valueOf(WINDOWS_SEPARATOR))) ? EMPTY : ext;
        }
    }
}
