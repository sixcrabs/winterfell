package org.winterfell.misc.indigo.renderer.ext.pptx.support;

/**
 * 常量
 * @author alex
 * @version v1.0 2020/11/18
 */
public final class Constants {

    private Constants() {}

    /**
     * regular expression: Chinese, letters, numbers and underscores
     */
    public static final String DEFAULT_GRAMMAR_REGEX = "((#)?[\\w\\u4e00-\\u9fa5]+(\\.[\\w\\u4e00-\\u9fa5]+)*)?";

    /**
     * 匹配 `@var` or `#var` or `*var`
     */
    public static final String SIGN_REGEX="(\u0000|@|\\#|\\*|/)?";

    /**
     * tag prefix
     */
    public static final String GRAMMAR_PREFIX = "{{";

    /**
     * tag suffix
     */
    public static final String GRAMMAR_SUFFIX = "}}";

    /**
     * 空字符
     */
    public static final char EMPTY_CHAR = '\0';




}