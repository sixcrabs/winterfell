package org.winterfell.samples.starter.client;

import org.apache.commons.lang3.RandomUtils;
import org.winterfell.samples.starter.client.domain.MrcTestDTO;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/1/20
 */
public class TestDateClientTest extends BaseTest {

    @Resource
    private TestDateClient testDateClient;

    @Test
    void getDto() {
        List<MrcTestDTO> mrcTestDTOS = testDateClient.getDto("alex".concat(String.valueOf(RandomUtils.nextInt(1, 100))));
        System.out.println(mrcTestDTOS);
    }
}