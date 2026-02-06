package org.winterfell.shared.as.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/9
 */
@Data
@ConfigurationProperties(prefix = "application.jackson")
public class JacksonFormatProperties {

    /**
     * localDate 格式化
     */
    private String localDateFormat = "yyyy-MM-dd";

    /**
     * localTime 格式化
     */
    private String localTimeFormat = "HH:mm:ss";
}
