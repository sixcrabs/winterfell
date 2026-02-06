package org.winterfell.shared.as.config;

import org.winterfell.shared.as.openapi.OpenApiProperties;
import lombok.Data;
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
@ConfigurationProperties(prefix = "application")
public class AppCustomProperties {

    private OpenApiProperties openapi = new OpenApiProperties();

    private JacksonFormatProperties jackson = new JacksonFormatProperties();

    private ResponseProperties resp = new ResponseProperties();

}
