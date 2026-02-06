package org.winterfell.minio.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/12/8
 */
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    public String getEndpoint() {
        return endpoint;
    }

    public MinioProperties setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public MinioProperties setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public MinioProperties setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }
}
