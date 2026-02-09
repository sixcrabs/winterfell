package org.winterfell.misc.srpc.serializer;

import java.io.IOException;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/3/13
 */
public interface Serializer {

    /**
     * 序列化实现的名称
     * @return
     */
    String name();

    /**
     * 序列化 对象
     * @param obj
     * @return
     * @throws IOException
     */
    byte[] serialize(Object obj) throws IOException;

    /**
     * 反序列化
     * @param bytes
     * @return
     * @throws IOException
     */
    Object deserialize(byte[] bytes) throws IOException;
}
