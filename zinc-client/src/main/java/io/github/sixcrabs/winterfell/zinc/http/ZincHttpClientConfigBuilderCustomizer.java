package io.github.sixcrabs.winterfell.zinc.http;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
@FunctionalInterface
public interface ZincHttpClientConfigBuilderCustomizer {

    /**
     * Customize the {@link ZincHttpClientConfig.Builder}.
     * @param builder the builder to customize
     */
    void customize(ZincHttpClientConfig.Builder builder);
}