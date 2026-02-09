package org.winterfell.misc.oss.support.result;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/10
 */
public abstract class AbstractOssResult implements Serializable {

    private String object;

    private String bucket;

    private String versionId;

    private Map<String, String> userMetadata;


    public String getObject() {
        return object;
    }

    public AbstractOssResult setObject(String object) {
        this.object = object;
        return this;
    }

    public String getBucket() {
        return bucket;
    }

    public AbstractOssResult setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String getVersionId() {
        return versionId;
    }

    public AbstractOssResult setVersionId(String versionId) {
        this.versionId = versionId;
        return this;
    }

    public Map<String, String> getUserMetadata() {
        return userMetadata;
    }

    public AbstractOssResult setUserMetadata(Map<String, String> userMetadata) {
        this.userMetadata = userMetadata;
        return this;
    }

    @Override
    public String toString() {
        return "AbstractOssResult{" +
                "object='" + object + '\'' +
                ", bucket='" + bucket + '\'' +
                ", versionId='" + versionId + '\'' +
                ", userMetadata=" + userMetadata +
                '}';
    }
}