package org.winterfell.misc.srpc.serializer.impl;

import org.winterfell.misc.srpc.serializer.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * <p>
 * kryo 序列化
 * </p>
 *
 * @author Alex
 * @since 2025/3/13
 */
public class KryoSerializer implements Serializer {

    public static final String NAME = "kryo";
    private static final KryoPool pool = new KryoPool.Builder(Kryo::new).softReferences().build();
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Kryo kryo = null;
        try (Output output = new Output(baos)) {
            kryo = pool.borrow();
            kryo.setRegistrationRequired(false);
            kryo.writeClassAndObject(output, obj);
            output.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (kryo != null) {
                pool.release(kryo);
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Kryo kryo = null;
        try (Input ois = new Input(new ByteArrayInputStream(bytes))) {
            kryo = pool.borrow();
            kryo.setRegistrationRequired(false);
            return kryo.readClassAndObject(ois);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (kryo != null) {
                pool.release(kryo);
            }
        }
    }
}
