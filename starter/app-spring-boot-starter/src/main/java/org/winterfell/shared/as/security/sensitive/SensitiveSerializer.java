package org.winterfell.shared.as.security.sensitive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.util.Objects;

/**
 * <p>
 * 对敏感字段自定义 序列化
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/8/2
 */
public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private String pattern;

    private int[] groupIndices;

    private String mask;


    public SensitiveSerializer() {
    }

    public SensitiveSerializer(String pattern, int[] groupIndices, String mask) {
        this.pattern = pattern;
        this.groupIndices = groupIndices;
        this.mask = mask;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(SensitiveUtil.desensitize(value, pattern, groupIndices, mask));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty)
            throws JsonMappingException {
        if (beanProperty != null) {
            // 非 String 类直接跳过
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                Sensitive sensitiveInfo = beanProperty.getAnnotation(Sensitive.class);
                if (sensitiveInfo == null) {
                    sensitiveInfo = beanProperty.getContextAnnotation(Sensitive.class);
                }
                if (sensitiveInfo != null) {
                    SensitiveType type = sensitiveInfo.type();
                    if (!type.equals(SensitiveType.EMPTY)) {
                        // 优先使用指定的 type
                        return new SensitiveSerializer(type.getPattern(), type.getGroup(), type.getMask());
                    } else {
                        return new SensitiveSerializer(sensitiveInfo.pattern(), sensitiveInfo.group(), sensitiveInfo.mask());
                    }
                }
                return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
            }
        }
        return serializerProvider.findNullValueSerializer(null);
    }

}
