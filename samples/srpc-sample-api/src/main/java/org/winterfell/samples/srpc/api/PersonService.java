package org.winterfell.samples.srpc.api;

/**
 * <p>
 * .
 * </p>
 *
 * @author <a href="mailto:yingxiufeng@mlogcn.com">alex</a>
 * @version v1.0, 2019/12/20
 */
public interface PersonService {

    PersonDTO find(String name);

    String plus(String a, String b);

    void describe(PersonDTO dto);
}
