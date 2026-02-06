package org.winterfell.misc.remote.mrc.support;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * .定制 client 的重试参数
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/10/12
 */
@Target({})
@Retention(RUNTIME)
public @interface MrClientRetryParam {


    /**
     * 是否启用
     * @return
     */
    boolean enabled() default false;

    /**
     * 最大重试次数
     * @return
     */
    int maxAttempts() default 3;

    /**
     * 重试等待间隔时间
     * @return
     */
    int waitDurationInSeconds() default 2;


}
