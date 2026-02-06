package org.winterfell.samples.srpc.springboot.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/28
 */
@SpringBootApplication(scanBasePackages = {"cn.piesat.v","org.winterfell"})
public class SrpcServerApp {

    public static void main(String[] args) {
        SpringApplication.run(SrpcServerApp.class, args);
    }
}
