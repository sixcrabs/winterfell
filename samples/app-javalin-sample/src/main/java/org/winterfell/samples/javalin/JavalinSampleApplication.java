package org.winterfell.samples.javalin;

import org.winterfell.starter.javalin.JavalinApplication;
import org.winterfell.starter.javalin.annotation.JavalinComponentScan;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/26
 */
@JavalinComponentScan({"cn.piesat.v", "org.wnterfell"})
public class JavalinSampleApplication {

    public static void main(String[] args) {
        JavalinApplication.initialize(JavalinSampleApplication.class).run(args);
    }
}
