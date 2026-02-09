package org.winterfell.misc.oss.support.option;

import java.io.Serializable;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/10
 */
public class OssListObjectsOption implements Serializable {

    /**
     * 是否递归桶内对象
     */
    private boolean recursively;

    /**
     * 是否包含 版本id
     */
    private boolean includeVersions;

    /**
     * 是否返回用户元数据
     */
    private boolean includeUserMetadata;

    /**
     * 默认最大返回  100 条记录
     */
    private int maxKeys = 100;


    public boolean isIncludeUserMetadata() {
        return includeUserMetadata;
    }

    public OssListObjectsOption setIncludeUserMetadata(boolean includeUserMetadata) {
        this.includeUserMetadata = includeUserMetadata;
        return this;
    }

    public boolean isRecursively() {
        return recursively;
    }

    public OssListObjectsOption setRecursively(boolean recursively) {
        this.recursively = recursively;
        return this;
    }

    public boolean isIncludeVersions() {
        return includeVersions;
    }

    public OssListObjectsOption setIncludeVersions(boolean includeVersions) {
        this.includeVersions = includeVersions;
        return this;
    }

    public int getMaxKeys() {
        return maxKeys;
    }

    public OssListObjectsOption setMaxKeys(int maxKeys) {
        this.maxKeys = maxKeys;
        return this;
    }

    @Override
    public String toString() {
        return "OssListObjectsOption{" +
                "recursively=" + recursively +
                ", includeVersions=" + includeVersions +
                ", maxKeys=" + maxKeys +
                '}';
    }
}