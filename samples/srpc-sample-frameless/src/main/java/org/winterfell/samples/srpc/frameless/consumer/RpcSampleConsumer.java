package org.winterfell.samples.srpc.frameless.consumer;

import org.winterfell.misc.hutool.mini.MapUtil;
import org.winterfell.misc.hutool.mini.RandomUtil;
import org.winterfell.samples.srpc.api.PersonDTO;
import org.winterfell.samples.srpc.api.PersonService;
import org.winterfell.misc.srpc.RpcConsumerFactory;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/28
 */
public class RpcSampleConsumer {


    public static void main(String[] args) {
        RpcConsumerFactory consumerFactory = RpcConsumerFactory.builder()
                .address("127.0.0.1:5555")
                .accessToken("whosyourdaddy")
                .build();

        PersonService personService = consumerFactory.getObject(PersonService.class);
        int count = 20;
        for (int i = 0; i < count; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println(e.getLocalizedMessage());
            }
            PersonDTO person = personService.find(RandomUtil.randomString(4));
            if (!Objects.isNull(person)) {
                System.out.printf("num: %d, response: %s \n", i, person);
            }
        }
        personService.describe(new PersonDTO()
                .setAge(20)
                .setName("Jason Jr")
                .setAttributes(MapUtil.of("birthday", LocalDateTime.of(1990, 1, 1, 0, 0, 0))));
    }
}
