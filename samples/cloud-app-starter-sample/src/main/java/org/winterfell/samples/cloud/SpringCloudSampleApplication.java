package org.winterfell.samples.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/3/24
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "cn.piesat.v")
public class SpringCloudSampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudSampleApplication.class, args);
    }
}