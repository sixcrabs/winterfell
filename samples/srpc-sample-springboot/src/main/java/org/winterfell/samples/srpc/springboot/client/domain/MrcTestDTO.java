package org.winterfell.samples.srpc.springboot.client.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/1/20
 */
public class MrcTestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date createTime;

    private String name;

    private Integer age;

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
