package org.winterfell.misc.oss.support.option;

import org.winterfell.misc.oss.support.OssObjectNameConflicts;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * obs put option
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/10
 */
public class OssPutObjectOption {


    /**
     * 内容类型
     */
    private String contentType;

    private String contentEncoding;

    private Map<String, String> metadata = new HashMap<String, String>(1);

    private OssObjectNameConflicts nameConflicts = OssObjectNameConflicts.keep_both;


    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getContentType() {
        return contentType;
    }

    public OssPutObjectOption setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Map<String, String> getMetadata() {
        return metadata == null ? new HashMap<String, String>(1) : metadata;
    }

    public OssPutObjectOption setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    public OssObjectNameConflicts getNameConflicts() {
        return nameConflicts;
    }

    public OssPutObjectOption setNameConflicts(OssObjectNameConflicts nameConflicts) {
        this.nameConflicts = nameConflicts;
        return this;
    }

    public OssPutObjectOption() {
    }
}