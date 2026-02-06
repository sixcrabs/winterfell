package org.winterfell.samples.srpc.api;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author <a href="mailto:yingxiufeng@mlogcn.com">alex</a>
 * @version v1.0, 2019/12/20
 */
@Data
@Accessors(chain = true)
@ToString
public class PersonDTO implements Serializable {

    private static final long serialVersionUID = 7702920534629457322L;

    private String name;

    private Integer age;

    private Map attributes;

}
