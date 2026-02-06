package org.winterfell.samples.javalin.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/9/27
 */
@Data
@Accessors(chain = true)
public class Person {

    private String name;

    private Integer age;

    private String address;

}
