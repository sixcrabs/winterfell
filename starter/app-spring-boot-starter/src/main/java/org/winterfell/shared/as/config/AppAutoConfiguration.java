package org.winterfell.shared.as.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.winterfell.shared.as.advice.response.ResponseFactory;
import org.winterfell.shared.as.advice.response.ResponseFactoryImpl;
import org.winterfell.shared.as.security.ratelimit.RateLimiterAspect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * .生成 app.pid
 * </p>
 *
 * @author alex
 * @version v1.0 2022/9/5
 */
@AutoConfiguration(after = JacksonAutoConfiguration.class)
@EnableConfigurationProperties({AppCustomProperties.class, JacksonFormatProperties.class, ResponseProperties.class})
@EnableEncryptableProperties
public class AppAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ResponseFactory.class)
    public ResponseFactory responseFactory(AppCustomProperties appCustomProperties) {
        return new ResponseFactoryImpl(appCustomProperties.getResp());
    }

    @Bean
    public ApplicationPidFileWriter applicationPidFileWriter() {
        ApplicationPidFileWriter pidFileWriter = new ApplicationPidFileWriter("app.pid");
        pidFileWriter.setTriggerEventType(ApplicationStartedEvent.class);
        return pidFileWriter;
    }

    /**
     * 定制 jackson 的 ojbectmapper ，将设置的 dateFormat 应用于 LocalDate 等类型
     *
     * @return
     */
    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(JacksonProperties jacksonProperties,
                                                                                       AppCustomProperties appCustomProperties) {
        String dateFormat = jacksonProperties.getDateFormat();
        if (!StringUtils.hasText(dateFormat)) {
            dateFormat = "yyyy-MM-dd HH:mm:ss";
        }
        String finalDateFormat = dateFormat;
        JacksonFormatProperties formatProperties = appCustomProperties.getJackson();
        return builder ->
                builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .locale(jacksonProperties.getLocale())
                        .timeZone(jacksonProperties.getTimeZone())
                        .serializerByType(Timestamp.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(finalDateFormat)))
                        .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(finalDateFormat)))
                        .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(finalDateFormat)))
                        .serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(formatProperties.getLocalDateFormat())))
                        .deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(formatProperties.getLocalDateFormat())))
                        .serializerByType(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(formatProperties.getLocalTimeFormat())))
                        .deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(formatProperties.getLocalDateFormat())));
    }

    @Bean
    @ConditionalOnClass(RateLimiterAspect.class)
    public RateLimiterAspect rateLimiterAspect() {
        return new RateLimiterAspect();
    }

    /**
     * 自定义 jasypt 加密配置
     * @return
     */
    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(getPassword());
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

    /**
     * 从环境变量、配置中心等获取密钥
     * @return
     */
    private String getPassword() {
        // 优先从系统属性获取
        String password = System.getProperty("jasypt.encryptor.password");
        if (password != null) {
            return password;
        }
        // 其次从环境变量获取
        return System.getenv("JASYPT_PASSWORD");
    }

}