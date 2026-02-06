package org.winterfell.samples.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/18
 */
@SpringBootApplication(scanBasePackages = "cn.piesat")
public class SampleTongwebApplication extends SpringBootServletInitializer {

    /**
     *  这里表示使用外部的tomcat容器
     * @param builder
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意这里要指向原先用main方法执行的启动类
        return builder.sources(SampleTongwebApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleTongwebApplication.class, args);
    }
}
