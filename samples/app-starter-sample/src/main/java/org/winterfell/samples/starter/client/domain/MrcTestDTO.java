package org.winterfell.samples.starter.client.domain;

import com.google.common.base.MoreObjects;

import java.util.Date;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/1/20
 */
public class MrcTestDTO {

    private Date createTime;

    private String name;

    private Integer age;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("create_at", this.getCreateTime()).toString();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public MrcTestDTO setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getName() {
        return name;
    }

    public MrcTestDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public MrcTestDTO setAge(Integer age) {
        this.age = age;
        return this;
    }
}
