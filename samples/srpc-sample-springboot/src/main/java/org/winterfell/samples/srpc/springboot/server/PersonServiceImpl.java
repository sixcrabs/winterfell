package org.winterfell.samples.srpc.springboot.server;

import org.winterfell.misc.hutool.mini.RandomUtil;
import org.winterfell.samples.srpc.api.PersonDTO;
import org.winterfell.samples.srpc.api.PersonService;
import org.winterfell.srpc.starter.annotation.RpcProvider;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/28
 */
@RpcProvider
public class PersonServiceImpl implements PersonService {
    @Override
    public PersonDTO find(String name) {
        return new PersonDTO().setAge(RandomUtil.randomInt(20,50)).setName(name);
    }

    @Override
    public String plus(String a, String b) {
        return String.format("%s %s %s", a, b, RandomUtil.randomInt(20,50));
    }

    @Override
    public void describe(PersonDTO dto) {
        System.out.println(dto.toString());
    }
}
