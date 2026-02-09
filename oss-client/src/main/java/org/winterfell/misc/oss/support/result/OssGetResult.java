package org.winterfell.misc.oss.support.result;

import org.winterfell.misc.hutool.mini.io.FastByteArrayOutputStream;
import org.winterfell.misc.oss.OssClientException;
import org.winterfell.misc.oss.support.OssHelper;

import java.io.*;
import java.time.LocalDateTime;

/**
 * <p>
 * obs GetObject  result
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/10
 */
public class OssGetResult extends AbstractOssResult {

    /**
     * 对象内容
     */
    private byte[] content;

    private String contentType;

    private Long contentLength;

    private LocalDateTime lastModified;

    /**
     * 写入 output
     *
     * @param outputStream
     */
    public void write(OutputStream outputStream) {
        if (this.content.length > 0) {
            try {
                OssHelper.write(outputStream, true, this.content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 写入 file
     *
     * @param file
     */
    public void write(File file) throws OssClientException {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new OssClientException(e.getLocalizedMessage());
            }
        }
        // write
        try {
            write(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new OssClientException(e.getLocalizedMessage());
        }
    }

    public String getContentType() {
        return contentType;
    }

    public OssGetResult setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public OssGetResult setContentLength(Long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public OssGetResult setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public OssGetResult setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public OssGetResult setContent(InputStream stream) {
        try (FastByteArrayOutputStream byteArrayOutputStream = new FastByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = stream.read(buf)) != -1) {
                byteArrayOutputStream.write(buf, 0, len);
            }
            byteArrayOutputStream.flush();
            this.content = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }
        return this;
    }
}