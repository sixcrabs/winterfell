package org.winterfell.misc.oss.support.result;

import java.time.LocalDateTime;

/**
 * <p>
 * obs stat result
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/9
 */
public class OssStatResult extends AbstractOssResult {

    private String contentType;

    private Long size;

    private LocalDateTime lastModified;


    public OssStatResult(String contentType, Long size, LocalDateTime lastModified) {
        this.contentType = contentType;
        this.size = size;
        this.lastModified = lastModified;
    }

    public OssStatResult() {
    }

    public String getContentType() {
        return contentType;
    }

    public OssStatResult setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public OssStatResult setSize(Long size) {
        this.size = size;
        return this;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public OssStatResult setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    @Override
    public String toString() {
        return "OssStatResult{" +
                "contentType='" + contentType + '\'' +
                ", size=" + size +
                ", lastModified=" + lastModified +
                "} " + super.toString();
    }
}