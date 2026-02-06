package org.winterfell.misc.oss.support.result;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/10
 */
public class OssBucketResult implements Serializable {

    private String name;

    /**
     * bucket 创建日期
     */
    private LocalDateTime createAt;

    public OssBucketResult() {
    }

    public OssBucketResult(String name, LocalDateTime createAt) {
        this.name = name;
        this.createAt = createAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public OssBucketResult setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
        return this;
    }

    public String getName() {
        return name;
    }

    public OssBucketResult setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "OssBucketResult{" +
                "name='" + name + '\'' +
                ", createAt=" + createAt +
                '}';
    }
}