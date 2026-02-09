package org.winterfell.misc.srpc.serializer.impl;

import org.winterfell.misc.srpc.RpcException;
import org.winterfell.misc.srpc.serializer.Serializer;
import org.winterfell.misc.srpc.serializer.impl.support.LocalDateDeserializer;
import org.winterfell.misc.srpc.serializer.impl.support.LocalDateSerializer;
import org.winterfell.misc.srpc.serializer.impl.support.LocalDateTimeDeserializer;
import org.winterfell.misc.srpc.serializer.impl.support.LocalDateTimeSerializer;
import com.caucho.hessian.io.ExtSerializerFactory;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/3/13
 */
public class HessianSerializer implements Serializer {

    public static final String NAME = "hessian";

    private static SerializerFactory serializerFactory;

    static {
        ExtSerializerFactory extSerializerFactory = new ExtSerializerFactory();
        extSerializerFactory.addSerializer(java.time.LocalDateTime.class, new LocalDateTimeSerializer());
        extSerializerFactory.addDeserializer(java.time.LocalDateTime.class, new LocalDateTimeDeserializer());

        extSerializerFactory.addSerializer(LocalDate.class, new LocalDateSerializer());
        extSerializerFactory.addDeserializer(LocalDate.class, new LocalDateDeserializer());

        serializerFactory = new SerializerFactory();
        serializerFactory.addFactory(extSerializerFactory);
        // 允许未实现 Serializable 的类
        serializerFactory.setAllowNonSerializable(true);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(os);
        ho.setSerializerFactory(serializerFactory);
        try {
            ho.writeObject(obj);
            ho.flush();
            return os.toByteArray();
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            ho.close();
            os.close();
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Hessian2Input hi = new Hessian2Input(is);
        hi.setSerializerFactory(serializerFactory);
        try {
            return hi.readObject();
        } catch (IOException e) {
            throw new RpcException(e);
        } finally {
            hi.close();
            is.close();
        }
    }
}
