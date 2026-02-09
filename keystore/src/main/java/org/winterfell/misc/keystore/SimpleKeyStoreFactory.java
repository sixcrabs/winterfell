package org.winterfell.misc.keystore;

/**
 * <p>
 * factory for `SimpleKeyStore`
 * </p>
 *
 * @author Alex
 * @since 2025/10/21
 */
public class SimpleKeyStoreFactory {

    private SimpleKeyStoreFactory() {
    }

    private static class SimpleKeyStoreFactoryHolder {
        private static final SimpleKeyStoreFactory INSTANCE = new SimpleKeyStoreFactory();
    }

    public static SimpleKeyStoreFactory getInstance() {
        return SimpleKeyStoreFactoryHolder.INSTANCE;
    }

    /**
     * 获取 KeyStore
     *
     * @param properties
     * @return
     */
    public SimpleKeyStore getKeyStore(KeyStoreProperties properties) {
        SimpleKeyStore keyStore;
        switch (properties.getType()) {
            case RedisKeyStoreImpl.NAME:
                keyStore = new RedisKeyStoreImpl();
                ((RedisKeyStoreImpl) keyStore).initialize(properties);
                break;
            case MapdbKeyStoreImpl.NAME:
                keyStore = new MapdbKeyStoreImpl();
                ((MapdbKeyStoreImpl) keyStore).initialize(properties);
                break;
            case MvStoreKeyStoreImpl.NAME:
                keyStore = new MvStoreKeyStoreImpl();
                ((MvStoreKeyStoreImpl) keyStore).initialize(properties);
                break;
            case FastutilKeyStoreImpl.NAME:
                keyStore = new FastutilKeyStoreImpl();
                ((FastutilKeyStoreImpl) keyStore).initialize(properties);
                break;
            case MemoryKeyStoreImpl.NAME:
            default:
                keyStore = new MemoryKeyStoreImpl();
                ((MemoryKeyStoreImpl) keyStore).initialize(properties);
        }
        return keyStore;
    }
}