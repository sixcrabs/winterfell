package org.winterfell.misc.remote.mrc.autoconfigure;

import okhttp3.OkHttpClient;

/**
 * 定制 http client 的 build 参数
 * @author alex
 * @version v1.0 2020/11/13
 */
@FunctionalInterface
public interface MrClientConfigBuilderCustomizer {

    /**
     * 定制 http client 的 build 参数
     *
     * @param builder {@link OkHttpClient.Builder}
     */
    void customize(OkHttpClient.Builder builder);
}
