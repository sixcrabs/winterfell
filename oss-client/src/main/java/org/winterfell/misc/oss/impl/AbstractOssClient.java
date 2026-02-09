package org.winterfell.misc.oss.impl;


import org.winterfell.misc.oss.OssClient;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/9
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractOssClient implements OssClient {


    protected AbstractOssClient(Builder builder) {
    }


    @SuppressWarnings("unchecked")
    protected static abstract class Builder<T extends OssClient, K> {

        /**
         * 连接地址
         */
        protected String endpoint;

        /**
         * 访问 key
         */
        protected String accessKey;

        /**
         * 访问密钥
         */
        protected String secretKey;

        /**
         * 区域 可为空
         */
        protected String region;


        public K setEndpoint(String  endpoint) {
            this.endpoint = endpoint;
            return (K) this;
        }

        public K setAccessKey(String accessKey) {
            this.accessKey = accessKey;
            return (K) this;
        }

        public K setSecretKey(String secretKey) {
            this.secretKey = secretKey;
            return (K) this;
        }

        public K setRegion(String region) {
            this.region = region;
            return (K) this;
        }

        /**
         * 生成实现
         * @return
         */
        abstract public T build();
    }
}