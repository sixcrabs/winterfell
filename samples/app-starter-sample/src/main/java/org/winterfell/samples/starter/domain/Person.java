package org.winterfell.samples.starter.domain;

import org.winterfell.shared.as.advice.RespAdviceExclude;
import org.winterfell.shared.as.security.sensitive.Sensitive;
import org.winterfell.shared.as.security.sensitive.SensitiveType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/18
 */
@Schema(name = "人员实体", title = "标题")
@RespAdviceExclude
public class Person implements Serializable {

    private String id;

    @Schema(title = "姓名")
    private String name;

    private Integer age;

    @Sensitive(type = SensitiveType.EMPTY, pattern = "(.{3})(.{6})(.{3})(.+)", group = {2, 4}, mask = "x")
    private String address;

    private String slogan;

    private LocalDateTime now = LocalDateTime.now();

    private Date birthday = new Date();

    private LocalDate dateNow = LocalDate.now();

    private LocalTime timeNow = LocalTime.now();

    public LocalDate getDateNow() {
        return dateNow;
    }

    public Person setDateNow(LocalDate dateNow) {
        this.dateNow = dateNow;
        return this;
    }

    public LocalTime getTimeNow() {
        return timeNow;
    }

    public Person setTimeNow(LocalTime timeNow) {
        this.timeNow = timeNow;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Person setAddress(String address) {
        this.address = address;
        return this;
    }

    public Date getBirthday() {
        return birthday;
    }

    public Person setBirthday(Date birthday) {
        this.birthday = birthday;
        return this;
    }

    public LocalDateTime getNow() {
        return now;
    }

    public Person setNow(LocalDateTime now) {
        this.now = now;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public Person setAge(Integer age) {
        this.age = age;
        return this;
    }

    public String getId() {
        return id;
    }

    public Person setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public String getSlogan() {
        return slogan;
    }

    public Person setSlogan(String slogan) {
        this.slogan = slogan;
        return this;
    }
}
