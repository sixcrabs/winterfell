package org.winterfell.minio.starter;

import com.google.common.base.Preconditions;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/4/24
 */
@Configuration
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties(MinioProperties.class)
public class MinioAutoConfiguration {


    private final MinioProperties properties;

    public MinioAutoConfiguration(MinioProperties properties) {
        this.properties = properties;
    }

    @Bean
    public MinioHighLevelClient minioHighLevelClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        Preconditions.checkNotNull(properties, "[minio] minio 配置信息不能为空");
        return MinioHighLevelClient.builder().minioClient(minioClient).build();
    }
}
