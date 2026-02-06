package org.winterfell.shared.as.openapi.knife4j.util;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/9/25
 */
@Slf4j
public final class EnvironmentUtils {

    private EnvironmentUtils() {
    }

    /**
     * 处理程序contextPath
     *
     * @param environment 环境变量
     * @return contextPath
     * @since v4.4.0
     */
    public static String resolveContextPath(Environment environment) {
        String contextPath = "";
        // Spring Boot 1.0
        String v1BasePath = environment.getProperty("server.context-path");
        // Spring Boot 2.0 & 3.0
        String basePath = environment.getProperty("server.servlet.context-path");
        if (StrUtil.isNotBlank(v1BasePath) && !"/".equals(v1BasePath)) {
            contextPath = v1BasePath;
        } else if (StrUtil.isNotBlank(basePath) && !"/".equals(basePath)) {
            contextPath = basePath;
        }
        return contextPath;
    }

    /**
     * get String property
     *
     * @param environment  Spring Context Environment
     * @param key          hash-key
     * @param defaultValue default
     * @return 属性
     */
    public static String resolveString(Environment environment, String key, String defaultValue) {
        if (environment != null) {
            String envValue = environment.getProperty(key);
            if (StrUtil.isNotBlank(envValue)) {
                return envValue;
            }
        }
        return defaultValue;
    }

    /**
     * 获取int类型的值
     *
     * @param environment  环境变量
     * @param key          变量
     * @param defaultValue 默认值
     * @return int属性
     */
    public static Integer resolveInt(Environment environment, String key, Integer defaultValue) {
        if (environment != null) {
            return Integer.parseInt(Objects.toString(environment.getProperty(key, String.valueOf(defaultValue)), String.valueOf(defaultValue)));
            // return Integer.parseInt(Objects.toString(environment.getProperty(key)), defaultValue);
        }
        return defaultValue;
    }

    /**
     * 获取bool值
     *
     * @param environment  环境变量
     * @param key          变量
     * @param defaultValue 默认值
     * @return bool
     */
    public static Boolean resolveBool(Environment environment, String key, Boolean defaultValue) {
        if (environment != null) {
            return Boolean.valueOf(Objects.toString(environment.getProperty(key, defaultValue.toString()), defaultValue.toString()));
        }
        return defaultValue;
    }
}
