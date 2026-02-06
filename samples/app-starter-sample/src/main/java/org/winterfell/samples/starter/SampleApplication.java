package org.winterfell.samples.starter;

import org.winterfell.misc.remote.mrc.EnableMrClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/18
 */
@SpringBootApplication(scanBasePackages = "org.winterfell")
@EnableMrClients(basePackages = "org.winterfell.samples.starter.client")
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}