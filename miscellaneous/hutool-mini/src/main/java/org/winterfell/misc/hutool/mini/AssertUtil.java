package org.winterfell.misc.hutool.mini;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/7
 */
public final class AssertUtil {


    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.notNull(clazz);
     * </pre>
     *
     * @param <T> 被检查对象类型
     * @param object 被检查对象
     * @return 非空对象
     * @throws NullPointerException if the object is {@code null}
     */
    public static <T> T notNull(T object) throws NullPointerException {
        return notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }


    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link IllegalArgumentException} 异常 Assert that an object is not {@code null} .
     *
     * <pre class="code">
     * Assert.notNull(clazz, "The class must not be null");
     * </pre>
     *
     * @param <T> 被检查对象泛型类型
     * @param object 被检查对象
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params 参数
     * @return 被检查后的对象
     * @throws NullPointerException if the object is {@code null}
     */
    public static <T> T notNull(T object, String errorMsgTemplate, Object... params) throws NullPointerException {
        if (object == null) {
            throw new NullPointerException(StringUtil.format(errorMsgTemplate, params));
        }
        return object;
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notBlank(name, "Name must not be blank");
     * </pre>
     *
     * @param text 被检查字符串
     * @return 非空字符串
     * @see StringUtil#isNotBlank(CharSequence)
     * @throws IllegalArgumentException 被检查字符串为空白
     */
    public static String notBlank(String text) throws IllegalArgumentException {
        return notBlank(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * AssertUtil.notBlank(name, "Name must not be blank");
     * </pre>
     *
     * @param text 被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params 参数
     * @return 非空字符串
     * @see StringUtil#isNotBlank(CharSequence)
     * @throws IllegalArgumentException 被检查字符串为空白
     */
    public static String notBlank(String text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (StringUtil.isBlank(text)) {
            throw new IllegalArgumentException(StringUtil.format(errorMsgTemplate, params));
        }
        return text;
    }

    /**
     * 检查给定字符串是否为空，为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notEmpty(name);
     * </pre>
     *
     * @param text 被检查字符串
     * @return 被检查的字符串
     * @see StringUtil#isNotEmpty(CharSequence)
     * @throws IllegalArgumentException 被检查字符串为空
     */
    public static String notEmpty(String text) throws IllegalArgumentException {
        return notEmpty(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }

    /**
     * 检查给定字符串是否为空，为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notEmpty(name, "Name must not be empty");
     * </pre>
     *
     * @param text 被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params 参数
     * @return 非空字符串
     * @see StringUtil#isNotEmpty(CharSequence)
     * @throws IllegalArgumentException 被检查字符串为空
     */
    public static String notEmpty(String text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (StringUtil.isEmpty(text)) {
            throw new IllegalArgumentException(StringUtil.format(errorMsgTemplate, params));
        }
        return text;
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, "The value must be greater than zero");
     * </pre>
     *
     * @param expression 波尔值
     * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
     * @param params 参数列表
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue(boolean expression, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (false == expression) {
            throw new IllegalArgumentException(StringUtil.format(errorMsgTemplate, params));
        }
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, "The value must be greater than zero");
     * </pre>
     *
     * @param expression 波尔值
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue(boolean expression) throws IllegalArgumentException {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isFalse(i &lt; 0, "The value must be greater than zero");
     * </pre>
     *
     * @param expression 波尔值
     * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
     * @param params 参数列表
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isFalse(boolean expression, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        if (expression) {
            throw new IllegalArgumentException(StringUtil.format(errorMsgTemplate, params));
        }
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isFalse(i &lt; 0);
     * </pre>
     *
     * @param expression 波尔值
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isFalse(boolean expression) throws IllegalArgumentException {
        isFalse(expression, "[Assertion failed] - this expression must be false");
    }
}
