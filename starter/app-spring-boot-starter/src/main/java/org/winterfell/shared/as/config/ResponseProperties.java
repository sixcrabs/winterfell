package org.winterfell.shared.as.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/10
 */
@Data
@ConfigurationProperties(prefix = "application.resp")
public class ResponseProperties {

    /**
     * 请求头排除
     */
    private List<String> requestHeadersExclude = Collections.singletonList("X-Micro-Rest-Client");

    /**
     * 默认的成功code
     */
    private int successCode = 0;

    /**
     * 默认的成功消息
     */
    private String successMsg = "success";

    /**
     * 默认的失败 code
     */
    private int failCode = 9999;

    /**
     * 默认的失败消息
     */
    private String failMsg = "Unknown error";


}
