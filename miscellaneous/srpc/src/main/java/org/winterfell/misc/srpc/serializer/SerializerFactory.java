package org.winterfell.misc.srpc.serializer;

import org.winterfell.misc.srpc.serializer.impl.HessianSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * <p>
 * 获取指定名称的 序列化实现
 * </p>
 *
 * @author Alex
 * @since 2025/3/13
 */
public class SerializerFactory {

    private static final Map<String, Serializer> REPO = new HashMap<>();

    private SerializerFactory() {
        ServiceLoader<Serializer> serializers = ServiceLoader.load(Serializer.class);
        for (Serializer serializer : serializers) {
            REPO.put(serializer.name(), serializer);
        }
    }

    private static final class Holder {
        private static final SerializerFactory INSTANCE = new SerializerFactory();
    }

    public static SerializerFactory getInstance() {
        return Holder.INSTANCE;
    }

    public Serializer getSerializer(String name) {
        Serializer serializer = REPO.get(name);
        return Objects.isNull(serializer) ? new HessianSerializer() : serializer;
    }
}
