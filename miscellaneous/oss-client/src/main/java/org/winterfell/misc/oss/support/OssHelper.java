package org.winterfell.misc.oss.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.*;
import java.util.Objects;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/10
 */
public final class OssHelper {

    private static final Logger logger = LoggerFactory.getLogger(OssHelper.class);

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

    private static final String LOCAL_ZONE_ID = "Asia/Shanghai";

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    public static String getContentType(File file) {
        String contentType = null;
        try {
            contentType = Files.probeContentType(file.toPath());
            if (Objects.isNull(contentType)) {
                contentType = new MimetypesFileTypeMap().getContentType(file);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return contentType;
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

    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of(LOCAL_ZONE_ID));
    }

    /**
     * to timestamp
     *
     * @param localDateTime
     * @return
     */
    public static long toTimestamp(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.of(LOCAL_ZONE_ID)).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * to timestamp
     *
     * @param date
     * @return
     */
    public static long toTimeStamp(LocalDate date) {
        Instant instant = LocalDateTime.of(date, LocalTime.of(0, 0, 0))
                .atZone(ZoneId.of(LOCAL_ZONE_ID)).toInstant();
        return instant.toEpochMilli();
    }


    /**
     * now to timestamp
     *
     * @return
     */
    public static long toTimeStamp() {
        return toTimestamp(now());
    }

    /**
     * 将byte[]写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param content    写入的内容
     * @throws IOException IO异常
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
}