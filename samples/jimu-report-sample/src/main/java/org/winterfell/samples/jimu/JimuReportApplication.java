package org.winterfell.samples.jimu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/5/10
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class JimuReportApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(JimuReportApplication.class, args);
        Environment env = application.getEnvironment();
        String port = env.getProperty("server.port");
        String path = env.containsProperty("server.servlet.context-path")?env.getProperty("server.servlet.context-path"):"";
        System.out.print("\n----------------------------------------------------------\n\t" +
                "Application JimuReport Demo is running! Access URL:\n\t" +
                "Local: \t\thttp://localhost:" + port + path + "/jmreport/list\n\t" +
                "----------------------------------------------------------");
    }

}
