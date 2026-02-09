package org.winterfell.misc.srpc.serializer.impl;

import org.winterfell.misc.srpc.serializer.Serializer;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.config.Language;

import java.io.IOException;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/3/13
 */
public class FurySerializer implements Serializer {

    public static final String NAME = "fury";

    private final static ThreadSafeFury fury = Fury.builder().withLanguage(Language.JAVA)
            // Allow to deserialize objects unknown types, more flexible
            // but may be insecure if the classes contains malicious code.
            .withRefTracking(true)
            .requireClassRegistration(false)
            .buildThreadSafeFury();

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public byte[] serialize(Object obj) throws IOException {
        return fury.serialize(obj);
    }

    @Override
    public Object deserialize(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return fury.deserialize(bytes);
    }
}
