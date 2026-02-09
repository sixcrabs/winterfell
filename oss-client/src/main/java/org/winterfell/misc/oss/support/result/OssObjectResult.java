package org.winterfell.misc.oss.support.result;

import java.time.LocalDateTime;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/10
 */
public class OssObjectResult extends AbstractOssResult {

    private Long size;

    private boolean isDir;

    private LocalDateTime lastModified;

    private String etag;

    public String getEtag() {
        return etag;
    }

    public OssObjectResult setEtag(String etag) {
        this.etag = etag;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public OssObjectResult setSize(Long size) {
        this.size = size;
        return this;
    }

    public boolean isDir() {
        return isDir;
    }

    public OssObjectResult setDir(boolean dir) {
        isDir = dir;
        return this;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public OssObjectResult setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    @Override
    public String toString() {
        return "OssObjectResult{" +
                "size=" + size +
                ", isDir=" + isDir +
                ", lastModified=" + lastModified +
                "} " + super.toString();
    }
}