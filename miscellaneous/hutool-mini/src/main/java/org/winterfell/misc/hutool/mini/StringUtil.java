package org.winterfell.misc.hutool.mini;

import org.winterfell.misc.hutool.mini.io.FileUtil;
import com.google.common.base.Joiner;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.winterfell.misc.hutool.mini.ArrayUtil.isEmpty;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/7
 */
public final class StringUtil {

    public static final Joiner JOINER_SLASH = Joiner.on(StringUtil.SLASH);

    public static final int INDEX_NOT_FOUND = -1;

    public static final char C_SPACE = CharUtil.SPACE;
    public static final char C_TAB = CharUtil.TAB;
    public static final char C_DOT = CharUtil.DOT;
    public static final char C_SLASH = CharUtil.SLASH;
    public static final char C_BACKSLASH = CharUtil.BACKSLASH;
    public static final char C_CR = CharUtil.CR;
    public static final char C_LF = CharUtil.LF;
    public static final char C_UNDERLINE = CharUtil.UNDERLINE;
    public static final char C_COMMA = CharUtil.COMMA;
    public static final char C_DELIM_START = CharUtil.DELIM_START;
    public static final char C_DELIM_END = CharUtil.DELIM_END;
    public static final char C_BRACKET_START = CharUtil.BRACKET_START;
    public static final char C_BRACKET_END = CharUtil.BRACKET_END;
    public static final char C_COLON = CharUtil.COLON;

    public static final String SPACE = " ";
    public static final String TAB = "	";
    public static final String DOT = ".";
    public static final String DOUBLE_DOT = "..";
    public static final String SLASH = "/";
    public static final String BACKSLASH = "\\";
    public static final String EMPTY = "";
    public static final String CR = "\r";
    public static final String LF = "\n";
    public static final String CRLF = "\r\n";
    public static final String UNDERLINE = "_";
    public static final String DASHED = "-";
    public static final String COMMA = ",";
    public static final String DELIM_START = "{";
    public static final String DELIM_END = "}";
    public static final String BRACKET_START = "[";
    public static final String BRACKET_END = "]";
    public static final String COLON = ":";

    public static final String HTML_NBSP = "&nbsp;";
    public static final String HTML_AMP = "&amp;";
    public static final String HTML_QUOTE = "&quot;";
    public static final String HTML_APOS = "&apos;";
    public static final String HTML_LT = "&lt;";
    public static final String HTML_GT = "&gt;";

    public static final String EMPTY_JSON = "{}";

    /** ISO-8859-1 */
    public static final String ISO_8859_1 = "ISO-8859-1";
    /** UTF-8 */
    public static final String UTF_8 = "UTF-8";
    /** GBK */
    public static final String GBK = "GBK";

    /** ISO-8859-1 */
    public static final Charset CHARSET_ISO_8859_1 = StandardCharsets.ISO_8859_1;
    /** UTF-8 */
    public static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;
    /** GBK */
    public static final Charset CHARSET_GBK = Charset.forName(GBK);

    /**
     * 创建StringBuilder对象
     *
     * @return StringBuilder对象
     */
    public static StringBuilder builder() {
        return new StringBuilder();
    }

    /**
     * 转换为Charset对象
     * @param charsetName 字符集，为空则返回默认字符集
     * @return Charset
     * @throws UnsupportedCharsetException 编码不支持
     */
    public static Charset charset(String charsetName) throws UnsupportedCharsetException{
        return isBlank(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
    }

    /**
     * 清理空白字符
     *
     * @param str 被清理的字符串
     * @return 清理后的字符串
     */
    public static String cleanBlank(CharSequence str) {
        return filter(str, c -> false == CharUtil.isBlankChar(c));
    }

    /**
     * 比较两个字符串（大小写敏感）。
     *
     * <pre>
     * equals(null, null)   = true
     * equals(null, &quot;abc&quot;)  = false
     * equals(&quot;abc&quot;, null)  = false
     * equals(&quot;abc&quot;, &quot;abc&quot;) = true
     * equals(&quot;abc&quot;, &quot;ABC&quot;) = false
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是{@code null}，则返回{@code true}
     */
    public static boolean equals(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, false);
    }

    /**
     * 比较两个字符串（大小写不敏感）。
     *
     * <pre>
     * equalsIgnoreCase(null, null)   = true
     * equalsIgnoreCase(null, &quot;abc&quot;)  = false
     * equalsIgnoreCase(&quot;abc&quot;, null)  = false
     * equalsIgnoreCase(&quot;abc&quot;, &quot;abc&quot;) = true
     * equalsIgnoreCase(&quot;abc&quot;, &quot;ABC&quot;) = true
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是{@code null}，则返回{@code true}
     */
    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, true);
    }

    /**
     * 比较两个字符串是否相等，规则如下
     * <ul>
     *     <li>str1和str2都为{@code null}</li>
     *     <li>忽略大小写使用{@link String#equalsIgnoreCase(String)}判断相等</li>
     *     <li>不忽略大小写使用{@link String#contentEquals(CharSequence)}判断相等</li>
     * </ul>
     *
     * @param str1       要比较的字符串1
     * @param str2       要比较的字符串2
     * @param ignoreCase 是否忽略大小写
     * @return 如果两个字符串相同，或者都是{@code null}，则返回{@code true}
     * @since 3.2.0
     */
    public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
        if (null == str1) {
            // 只有两个都为null才判断相等
            return str2 == null;
        }
        if (null == str2) {
            // 字符串2空，字符串1非空，直接false
            return false;
        }

        if (ignoreCase) {
            return str1.toString().equalsIgnoreCase(str2.toString());
        } else {
            return str1.toString().contentEquals(str2);
        }
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String utf8Str(Object obj) {
        return str(obj, CHARSET_UTF_8);
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj 对象
     * @param charsetName 字符集
     * @return 字符串
     */
    public static String str(Object obj, String charsetName) {
        return str(obj, Charset.forName(charsetName));
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj 对象
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[]) {
            return str((byte[]) obj, charset);
        } else if (obj instanceof Byte[]) {
            return str((Byte[]) obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return str((ByteBuffer) obj, charset);
        } else if (ArrayUtil.isArray(obj)) {
            return ArrayUtil.toString(obj);
        }

        return obj.toString();
    }

    /**
     * 解码字节码
     *
     * @param data 字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String str(byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        if (null == charset) {
            return new String(data);
        }
        return new String(data, charset);
    }

    /**
     * 解码字节码
     *
     * @param data 字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String str(Byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        byte[] bytes = new byte[data.length];
        Byte dataByte;
        for (int i = 0; i < data.length; i++) {
            dataByte = data[i];
            bytes[i] = (null == dataByte) ? -1 : dataByte.byteValue();
        }

        return str(bytes, charset);
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data 数据
     * @param charset 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String str(ByteBuffer data, Charset charset) {
        if (null == charset) {
            charset = Charset.defaultCharset();
        }
        return charset.decode(data).toString();
    }


    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params 参数值
     * @return 格式化后的文本
     */
    public static String format(CharSequence template, Object... params) {
        if (null == template) {
            return null;
        }
        if (ArrayUtil.isEmpty(params) || isBlank(template)) {
            return template.toString();
        }
        return StrFormatter.format(template.toString(), params);
    }

    /**
     * 格式化文本，使用 {varName} 占位<br>
     * map = {a: "aValue", b: "bValue"} format("{a} and {b}", map) ---=》 aValue and bValue
     *
     * @param template 文本模板，被替换的部分用 {key} 表示
     * @param map 参数值对
     * @return 格式化后的文本
     */
    public static String format(CharSequence template, Map<?, ?> map) {
        if (null == template) {
            return null;
        }
        if (null == map || map.isEmpty()) {
            return template.toString();
        }

        String template2 = template.toString();
        String value;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            value = utf8Str(entry.getValue());
            if(null != value) {
                template2 = replace(template2, "{" + entry.getKey() + "}", value);
            }
        }
        return template2;
    }

    /**
     * 替换字符串中的指定字符串
     *
     * @param str 字符串
     * @param searchStr 被查找的字符串
     * @param replacement 被替换的字符串
     * @return 替换后的字符串
     * @since 4.0.3
     */
    public static String replace(CharSequence str, CharSequence searchStr, CharSequence replacement) {
        return replace(str, 0, searchStr, replacement, false);
    }


    /**
     * 替换字符串中的指定字符串
     *
     * @param str 字符串
     * @param fromIndex 开始位置（包括）
     * @param searchStr 被查找的字符串
     * @param replacement 被替换的字符串
     * @param ignoreCase 是否忽略大小写
     * @return 替换后的字符串
     * @since 4.0.3
     */
    public static String replace(CharSequence str, int fromIndex, CharSequence searchStr, CharSequence replacement, boolean ignoreCase) {
        if (isEmpty(str) || isEmpty(searchStr)) {
            return str(str);
        }
        if (null == replacement) {
            replacement = EMPTY;
        }

        final int strLength = str.length();
        final int searchStrLength = searchStr.length();
        if (fromIndex > strLength) {
            return str(str);
        } else if (fromIndex < 0) {
            fromIndex = 0;
        }

        final StrBuilder result = StrBuilder.create(strLength + 16);
        if (0 != fromIndex) {
            result.append(str.subSequence(0, fromIndex));
        }

        int preIndex = fromIndex;
        int index = fromIndex;
        while ((index = indexOf(str, searchStr, preIndex, ignoreCase)) > -1) {
            result.append(str.subSequence(preIndex, index));
            result.append(replacement);
            preIndex = index + searchStrLength;
        }

        if (preIndex < strLength) {
            // 结尾部分
            result.append(str.subSequence(preIndex, strLength));
        }
        return result.toString();
    }


    /**
     * 指定范围内反向查找字符串
     *
     * @param str 字符串
     * @param searchStr 需要查找位置的字符串
     * @param fromIndex 起始位置
     * @param ignoreCase 是否忽略大小写
     * @return 位置
     * @since 3.2.1
     */
    public static int indexOf(final CharSequence str, CharSequence searchStr, int fromIndex, boolean ignoreCase) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }

        final int endLimit = str.length() - searchStr.length() + 1;
        if (fromIndex > endLimit) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return fromIndex;
        }

        if (false == ignoreCase) {
            // 不忽略大小写调用JDK方法
            return str.toString().indexOf(searchStr.toString(), fromIndex);
        }

        for (int i = fromIndex; i < endLimit; i++) {
            if (isSubEquals(str, i, searchStr, 0, searchStr.length(), true)) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 截取两个字符串的不同部分（长度一致），判断截取的子串是否相同<br>
     * 任意一个字符串为null返回false
     *
     * @param str1 第一个字符串
     * @param start1 第一个字符串开始的位置
     * @param str2 第二个字符串
     * @param start2 第二个字符串开始的位置
     * @param length 截取长度
     * @param ignoreCase 是否忽略大小写
     * @return 子串是否相同
     * @since 3.2.1
     */
    public static boolean isSubEquals(CharSequence str1, int start1, CharSequence str2, int start2, int length, boolean ignoreCase) {
        if (null == str1 || null == str2) {
            return false;
        }

        return str1.toString().regionMatches(ignoreCase, start1, str2.toString(), start2, length);
    }

    /**
     * {@link CharSequence} 转为字符串，null安全
     *
     * @param cs {@link CharSequence}
     * @return 字符串
     */
    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }

    /**
     * 将byte数组转为字符串
     *
     * @param bytes byte数组
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(byte[] bytes, String charset) {
        return str(bytes, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 编码字符串，编码为UTF-8
     *
     * @param str 字符串
     * @return 编码后的字节码
     */
    public static byte[] utf8Bytes(CharSequence str) {
        return bytes(str, CHARSET_UTF_8.name());
    }

    /**
     * 编码字符串<br>
     * 使用系统默认编码
     *
     * @param str 字符串
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str) {
        return bytes(str, Charset.defaultCharset());
    }

    /**
     * 编码字符串
     *
     * @param str 字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str, String charset) {
        return bytes(str, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }


    /**
     * 编码字符串
     *
     * @param str 字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str, Charset charset) {
        if (str == null) {
            return null;
        }

        if (null == charset) {
            return str.toString().getBytes();
        }
        return str.toString().getBytes(charset);
    }

    /**
     * 系统字符集编码，如果是Windows，则默认为GBK编码，否则取 {@link #defaultCharsetName()}
     *
     * @return 系统字符集编码
     * @since 3.1.2
     */
    public static String systemCharsetName() {
        return systemCharset().name();
    }


    /**
     * 系统字符集编码，如果是Windows，则默认为GBK编码，否则取 {@link #defaultCharsetName()}
     *
     * @return 系统字符集编码
     * @since 3.1.2
     */
    public static Charset systemCharset() {
        return FileUtil.isWindows() ? CHARSET_GBK : defaultCharset();
    }

    /**
     * 系统默认字符集编码
     *
     * @return 系统字符集编码
     */
    public static String defaultCharsetName() {
        return defaultCharset().name();
    }

    /**
     * 系统默认字符集编码
     *
     * @return 系统字符集编码
     */
    public static Charset defaultCharset() {
        return Charset.defaultCharset();
    }

    /**
     * 字符串是否为空，空的定义如下:<br>
     * 1、为null <br>
     * 2、为""<br>
     *
     * @param str 被检测的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 字符串是否为非空白 空白的定义如下： <br>
     * 1、不为null <br>
     * 2、不为""<br>
     *
     * @param str 被检测的字符串
     * @return 是否为非空
     */
    public static boolean isNotEmpty(CharSequence str) {
        return false == isEmpty(str);
    }

    /**
     * 去掉指定前缀
     *
     * @param str 字符串
     * @param prefix 前缀
     * @return 切掉后的字符串，若前缀不是 preffix， 返回原字符串
     */
    public static String removePrefix(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str(str);
        }

        final String str2 = str.toString();
        if (str2.startsWith(prefix.toString())) {
            // 截取后半段
            return subSuf(str2, prefix.length());
        }
        return str2;
    }


    /**
     * 去掉指定后缀
     *
     * @param str 字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    public static String removeSuffix(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str(str);
        }

        final String str2 = str.toString();
        if (str2.endsWith(suffix.toString())) {
            // 截取前半段
            return subPre(str2, str2.length() - suffix.length());
        }
        return str2;
    }

    /**
     * 忽略大小写去掉指定前缀
     *
     * @param str 字符串
     * @param prefix 前缀
     * @return 切掉后的字符串，若前缀不是 prefix， 返回原字符串
     */
    public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str(str);
        }

        final String str2 = str.toString();
        if (str2.toLowerCase().startsWith(prefix.toString().toLowerCase())) {
            // 截取后半段
            return subSuf(str2, prefix.length());
        }
        return str2;
    }

    // ------------------------------------------------------------------------ repeat

    /**
     * 重复某个字符
     *
     * <pre>
     * StrUtil.repeat('e', 0)  = ""
     * StrUtil.repeat('e', 3)  = "eee"
     * StrUtil.repeat('e', -2) = ""
     * </pre>
     *
     * @param c     被重复的字符
     * @param count 重复的数目，如果小于等于0则返回""
     * @return 重复字符字符串
     */
    public static String repeat(char c, int count) {
        if (count <= 0) {
            return EMPTY;
        }

        char[] result = new char[count];
        for (int i = 0; i < count; i++) {
            result[i] = c;
        }
        return new String(result);
    }

    /**
     * 重复某个字符串
     *
     * @param str   被重复的字符
     * @param count 重复的数目
     * @return 重复字符字符串
     */
    public static String repeat(CharSequence str, int count) {
        if (null == str) {
            return null;
        }
        if (count <= 0 || str.length() == 0) {
            return EMPTY;
        }
        if (count == 1) {
            return str.toString();
        }

        // 检查
        final int len = str.length();
        final long longSize = (long) len * (long) count;
        final int size = (int) longSize;
        if (size != longSize) {
            throw new ArrayIndexOutOfBoundsException("Required String length is too large: " + longSize);
        }

        final char[] array = new char[size];
        str.toString().getChars(0, len, array, 0);
        int n;
        for (n = len; n < size - n; n <<= 1) {// n <<= 1相当于n *2
            System.arraycopy(array, 0, array, n, n);
        }
        System.arraycopy(array, 0, array, n, size - n);
        return new String(array);
    }

    /**
     * 重复某个字符串到指定长度
     *
     * @param str    被重复的字符
     * @param padLen 指定长度
     * @return 重复字符字符串
     * @since 4.3.2
     */
    public static String repeatByLength(CharSequence str, int padLen) {
        if (null == str) {
            return null;
        }
        if (padLen <= 0) {
            return EMPTY;
        }
        final int strLen = str.length();
        if (strLen == padLen) {
            return str.toString();
        } else if (strLen > padLen) {
            return subPre(str, padLen);
        }

        // 重复，直到达到指定长度
        final char[] padding = new char[padLen];
        for (int i = 0; i < padLen; i++) {
            padding[i] = str.charAt(i % strLen);
        }
        return new String(padding);
    }

    /**
     * 重复某个字符串并通过分界符连接
     *
     * <pre>
     * StrUtil.repeatAndJoin("?", 5, ",")   = "?,?,?,?,?"
     * StrUtil.repeatAndJoin("?", 0, ",")   = ""
     * StrUtil.repeatAndJoin("?", 5, null) = "?????"
     * </pre>
     *
     * @param str       被重复的字符串
     * @param count     数量
     * @param delimiter 分界符
     * @return 连接后的字符串
     * @since 4.0.1
     */
    public static String repeatAndJoin(CharSequence str, int count, CharSequence delimiter) {
        if (count <= 0) {
            return EMPTY;
        }
        final StringBuilder builder = new StringBuilder(str.length() * count);
        builder.append(str);
        count--;

        final boolean isAppendDelimiter = isNotEmpty(delimiter);
        while (count-- > 0) {
            if (isAppendDelimiter) {
                builder.append(delimiter);
            }
            builder.append(str);
        }
        return builder.toString();
    }


    /**
     * 切割指定位置之前部分的字符串
     *
     * @param string 字符串
     * @param toIndex 切割到的位置（不包括）
     * @return 切割后的剩余的前半部分字符串
     */
    public static String subPre(CharSequence string, int toIndex) {
        return sub(string, 0, toIndex);
    }

    /**
     * 切割指定位置之后部分的字符串
     *
     * @param string 字符串
     * @param fromIndex 切割开始的位置（包括）
     * @return 切割后后剩余的后半部分字符串
     */
    public static String subSuf(CharSequence string, int fromIndex) {
        if (isEmpty(string)) {
            return null;
        }
        return sub(string, fromIndex, string.length());
    }

    /**
     * 切割指定长度的后部分的字符串
     *
     * <pre>
     * StrUtil.subSufByLength("abcde", 3)      =    "cde"
     * StrUtil.subSufByLength("abcde", 0)      =    ""
     * StrUtil.subSufByLength("abcde", -5)     =    ""
     * StrUtil.subSufByLength("abcde", -1)     =    ""
     * StrUtil.subSufByLength("abcde", 5)       =    "abcde"
     * StrUtil.subSufByLength("abcde", 10)     =    "abcde"
     * StrUtil.subSufByLength(null, 3)               =    null
     * </pre>
     *
     * @param string 字符串
     * @param length 切割长度
     * @return 切割后后剩余的后半部分字符串
     * @since 4.0.1
     */
    public static String subSufByLength(CharSequence string, int length) {
        if (isEmpty(string)) {
            return null;
        }
        if (length <= 0) {
            return EMPTY;
        }
        return sub(string, -length, string.length());
    }

    /**
     * 截取字符串,从指定位置开始,截取指定长度的字符串<br>
     * author weibaohui
     *
     * @param input     原始字符串
     * @param fromIndex 开始的index,包括
     * @param length    要截取的长度
     * @return 截取后的字符串
     */
    public static String subWithLength(String input, int fromIndex, int length) {
        return sub(input, fromIndex, fromIndex + length);
    }

    /**
     * 改进JDK subString<br>
     * index从0开始计算，最后一个字符为-1<br>
     * 如果from和to位置一样，返回 "" <br>
     * 如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length<br>
     * 如果经过修正的index中from大于to，则互换from和to example: <br>
     * abcdefgh 2 3 =》 c <br>
     * abcdefgh 2 -3 =》 cde <br>
     *
     * @param str String
     * @param fromIndex 开始的index（包括）
     * @param toIndex 结束的index（不包括）
     * @return 字串
     */
    public static String sub(CharSequence str, int fromIndex, int toIndex) {
        if (isEmpty(str)) {
            return str(str);
        }
        int len = str.length();

        if (fromIndex < 0) {
            fromIndex = len + fromIndex;
            if (fromIndex < 0) {
                fromIndex = 0;
            }
        } else if (fromIndex > len) {
            fromIndex = len;
        }

        if (toIndex < 0) {
            toIndex = len + toIndex;
            if (toIndex < 0) {
                toIndex = len;
            }
        } else if (toIndex > len) {
            toIndex = len;
        }

        if (toIndex < fromIndex) {
            int tmp = fromIndex;
            fromIndex = toIndex;
            toIndex = tmp;
        }

        if (fromIndex == toIndex) {
            return EMPTY;
        }

        return str.toString().substring(fromIndex, toIndex);
    }

    /**
     * 字符串是否为非空白 空白的定义如下： <br>
     * 1、不为null <br>
     * 2、不为不可见字符（如空格）<br>
     * 3、不为""<br>
     *
     * @param str 被检测的字符串
     * @return 是否为非空
     */
    public static boolean isNotBlank(CharSequence str) {
        return false == isBlank(str);
    }

    /**
     * 字符串是否为空白 空白的定义如下： <br>
     * 1、为null <br>
     * 2、为不可见字符（如空格）<br>
     * 3、""<br>
     *
     * @param str 被检测的字符串
     * @return 是否为空
     */
    public static boolean isBlank(CharSequence str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            // 只要有一个非空字符即为非空字符串
            if (false == CharUtil.isBlankChar(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 如果对象是字符串是否为空白，空白的定义如下： <br>
     * 1、为null <br>
     * 2、为不可见字符（如空格）<br>
     * 3、""<br>
     *
     * @param obj 对象
     * @return 如果为字符串是否为空串
     * @since 3.3.0
     */
    public static boolean isBlankIfStr(Object obj) {
        if (null == obj) {
            return true;
        } else if (obj instanceof CharSequence) {
            return isBlank((CharSequence) obj);
        }
        return false;
    }


    /**
     * 是否包含空字符串
     *
     * @param strs 字符串列表
     * @return 是否包含空字符串
     */
    public static boolean hasBlank(CharSequence... strs) {
        if (ArrayUtil.isEmpty(strs)) {
            return true;
        }

        for (CharSequence str : strs) {
            if (isBlank(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 过滤字符串
     *
     * @param str    字符串
     * @param filter 过滤器，{@link Filter#accept(Object)}返回为{@code true}的保留字符
     * @return 过滤后的字符串
     * @since 5.4.0
     */
    public static String filter(CharSequence str, final Filter<Character> filter) {
        if (str == null || filter == null) {
            return str(str);
        }

        int len = str.length();
        final StringBuilder sb = new StringBuilder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (filter.accept(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 给定所有字符串是否为空白
     *
     * @param strs 字符串
     * @return 所有字符串是否为空白
     */
    public static boolean isAllBlank(CharSequence... strs) {
        if (ArrayUtil.isEmpty(strs)) {
            return true;
        }

        for (CharSequence str : strs) {
            if (isNotBlank(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将下划线方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。<br>
     * 例如：hello_world=》helloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String toCamelCase(CharSequence name) {
        if (null == name) {
            return null;
        }

        String name2 = name.toString();
        if (name2.contains(UNDERLINE)) {
            final StringBuilder sb = new StringBuilder(name2.length());
            boolean upperCase = false;
            for (int i = 0; i < name2.length(); i++) {
                char c = name2.charAt(i);

                if (c == CharUtil.UNDERLINE) {
                    upperCase = true;
                } else if (upperCase) {
                    sb.append(Character.toUpperCase(c));
                    upperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
            return sb.toString();
        } else {
            return name2;
        }
    }

    /**
     * 将驼峰式命名的字符串转换为下划线方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。<br>
     * 例如：
     * <pre>
     * HelloWorld=》hello_world
     * Hello_World=》hello_world
     * HelloWorld_test=》hello_world_test
     * </pre>
     *
     * @param str 转换前的驼峰式命名的字符串，也可以为下划线形式
     * @return 转换后下划线方式命名的字符串
     */
    public static String toUnderlineCase(CharSequence str) {
        return toSymbolCase(str, CharUtil.UNDERLINE);
    }

    /**
     * 将驼峰式命名的字符串转换为使用符号连接方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。<br>
     *
     * @param str 转换前的驼峰式命名的字符串，也可以为符号连接形式
     * @param symbol 连接符
     * @return 转换后符号连接方式命名的字符串
     * @since 4.0.10
     */
    public static String toSymbolCase(CharSequence str, char symbol) {
        if (str == null) {
            return null;
        }

        final int length = str.length();
        final StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < length; i++) {
            c = str.charAt(i);
            final Character preChar = (i > 0) ? str.charAt(i - 1) : null;
            if (Character.isUpperCase(c)) {
                //遇到大写字母处理
                final Character nextChar = (i < str.length() - 1) ? str.charAt(i + 1) : null;
                if (null != preChar && Character.isUpperCase(preChar)) {
                    //前一个字符为大写，则按照一个词对待
                    sb.append(c);
                } else if (null != nextChar && Character.isUpperCase(nextChar)) {
                    //后一个为大写字母，按照一个词对待
                    if(null != preChar && symbol != preChar) {
                        //前一个是非大写时按照新词对待，加连接符
                        sb.append(symbol);
                    }
                    sb.append(c);
                } else {
                    //前后都为非大写按照新词对待
                    if (null != preChar &&symbol != preChar) {
                        //前一个非连接符，补充连接符
                        sb.append(symbol);
                    }
                    sb.append(Character.toLowerCase(c));
                }
            } else {
                if(sb.length() > 0 && Character.isUpperCase(sb.charAt(sb.length() -1)) && symbol != c) {
                    //当结果中前一个字母为大写，当前为小写，说明此字符为新词开始（连接符也表示新词）
                    sb.append(symbol);
                }
                //小写或符号
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>，依然返回<code>null</code>。
     *
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>NumberUtil.isBlankChar</code> 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     *
     * <pre>
     * trim(null)          = null
     * trim(&quot;&quot;)            = &quot;&quot;
     * trim(&quot;     &quot;)       = &quot;&quot;
     * trim(&quot;abc&quot;)         = &quot;abc&quot;
     * trim(&quot;    abc    &quot;) = &quot;abc&quot;
     * </pre>
     *
     * @param str 要处理的字符串
     *
     * @return 除去头尾空白的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trim(CharSequence str) {
        return (null == str) ? null : trim(str, 0);
    }

    /**
     * 给定字符串数组全部做去首尾空格
     *
     * @param strs 字符串数组
     */
    public static void trim(String[] strs) {
        if (null == strs) {
            return;
        }
        String str;
        for (int i = 0; i < strs.length; i++) {
            str = strs[i];
            if (null != str) {
                strs[i] = str.trim();
            }
        }
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是{@code null}，返回<code>""</code>。
     *
     * <pre>
     * StrUtil.trimToEmpty(null)          = ""
     * StrUtil.trimToEmpty("")            = ""
     * StrUtil.trimToEmpty("     ")       = ""
     * StrUtil.trimToEmpty("abc")         = "abc"
     * StrUtil.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str 字符串
     * @return 去除两边空白符后的字符串, 如果为null返回""
     * @since 3.1.1
     */
    public static String trimToEmpty(CharSequence str) {
        return str == null ? EMPTY : trim(str);
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是{@code null}，返回<code>""</code>。
     *
     * <pre>
     * StrUtil.trimToNull(null)          = null
     * StrUtil.trimToNull("")            = null
     * StrUtil.trimToNull("     ")       = null
     * StrUtil.trimToNull("abc")         = "abc"
     * StrUtil.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str 字符串
     * @return 去除两边空白符后的字符串, 如果为空返回null
     * @since 3.2.1
     */
    public static String trimToNull(CharSequence str) {
        final String trimStr = trim(str);
        return EMPTY.equals(trimStr) ? null : trimStr;
    }

    /**
     * 字符串是否以给定字符开始
     *
     * @param str 字符串
     * @param c 字符
     * @return 是否开始
     */
    public static boolean startWith(CharSequence str, char c) {
        return c == str.charAt(0);
    }

    /**
     * 是否以指定字符串开头<br>
     * 如果给定的字符串和开头字符串都为null则返回true，否则任意一个值为null返回false
     *
     * @param str 被监测字符串
     * @param prefix 开头字符串
     * @param isIgnoreCase 是否忽略大小写
     * @return 是否以指定字符串开头
     */
    public static boolean startWith(CharSequence str, CharSequence prefix, boolean isIgnoreCase) {
        if (null == str || null == prefix) {
            if (null == str && null == prefix) {
                return true;
            }
            return false;
        }

        if (isIgnoreCase) {
            return str.toString().toLowerCase().startsWith(prefix.toString().toLowerCase());
        } else {
            return str.toString().startsWith(prefix.toString());
        }
    }

    /**
     * 是否以指定字符串开头
     *
     * @param str 被监测字符串
     * @param prefix 开头字符串
     * @return 是否以指定字符串开头
     */
    public static boolean startWith(CharSequence str, CharSequence prefix) {
        return startWith(str, prefix, false);
    }

    /**
     * 是否以指定字符串开头，忽略大小写
     *
     * @param str 被监测字符串
     * @param prefix 开头字符串
     * @return 是否以指定字符串开头
     */
    public static boolean startWithIgnoreCase(CharSequence str, CharSequence prefix) {
        return startWith(str, prefix, true);
    }

    /**
     * 除去字符串头部的空白，如果字符串是<code>null</code>，则返回<code>null</code>。
     *
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>CharUtil.isBlankChar</code> 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     *
     * <pre>
     * trimStart(null)         = null
     * trimStart(&quot;&quot;)           = &quot;&quot;
     * trimStart(&quot;abc&quot;)        = &quot;abc&quot;
     * trimStart(&quot;  abc&quot;)      = &quot;abc&quot;
     * trimStart(&quot;abc  &quot;)      = &quot;abc  &quot;
     * trimStart(&quot; abc &quot;)      = &quot;abc &quot;
     * </pre>
     *
     * @param str 要处理的字符串
     *
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimStart(CharSequence str) {
        return trim(str, -1);
    }

    /**
     * 除去字符串尾部的空白，如果字符串是<code>null</code>，则返回<code>null</code>。
     *
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>CharUtil.isBlankChar</code> 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     *
     * <pre>
     * trimEnd(null)       = null
     * trimEnd(&quot;&quot;)         = &quot;&quot;
     * trimEnd(&quot;abc&quot;)      = &quot;abc&quot;
     * trimEnd(&quot;  abc&quot;)    = &quot;  abc&quot;
     * trimEnd(&quot;abc  &quot;)    = &quot;abc&quot;
     * trimEnd(&quot; abc &quot;)    = &quot; abc&quot;
     * </pre>
     *
     * @param str 要处理的字符串
     *
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimEnd(CharSequence str) {
        return trim(str, 1);
    }

    /**
     * 除去字符串头尾部的空白符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     *
     * @param str 要处理的字符串
     * @param mode <code>-1</code>表示trimStart，<code>0</code>表示trim全部， <code>1</code>表示trimEnd
     *
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trim(CharSequence str, int mode) {
        if (str == null) {
            return null;
        }

        int length = str.length();
        int start = 0;
        int end = length;

        // 扫描字符串头部
        if (mode <= 0) {
            while ((start < end) && (CharUtil.isBlankChar(str.charAt(start)))) {
                start++;
            }
        }

        // 扫描字符串尾部
        if (mode >= 0) {
            while ((start < end) && (CharUtil.isBlankChar(str.charAt(end - 1)))) {
                end--;
            }
        }

        if ((start > 0) || (end < length)) {
            return str.toString().substring(start, end);
        }

        return str.toString();
    }



    /**
     * 切分字符串
     *
     * @param str 被切分的字符串
     * @param separator 分隔符
     * @return 字符串
     */
    public static String[] split(CharSequence str, CharSequence separator) {
        if (str == null) {
            return new String[] {};
        }

        final String separatorStr = (null == separator) ? null : separator.toString();
        return splitToArray(str.toString(), separatorStr, 0, false, false);
    }

    /**
     * 根据给定长度，将给定字符串截取为多个部分
     *
     * @param str 字符串
     * @param len 每一个小节的长度
     * @return 截取后的字符串数组
     */
    public static String[] split(CharSequence str, int len) {
        if (null == str) {
            return new String[] {};
        }
        return splitByLength(str.toString(), len);
    }

    /**
     * 切分字符串为字符串数组
     *
     * @param str 被切分的字符串
     * @param separator 分隔符字符
     * @param limit 限制分片数
     * @param isTrim 是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static String[] splitToArray(String str, String separator, int limit, boolean isTrim, boolean ignoreEmpty){
        return toArray(split(str, separator, limit, isTrim, ignoreEmpty));
    }


    /**
     * 根据给定长度，将给定字符串截取为多个部分
     *
     * @param str 字符串
     * @param len 每一个小节的长度
     * @return 截取后的字符串数组
     */
    public static String[] splitByLength(String str, int len) {
        int partCount = str.length() / len;
        int lastPartCount = str.length() % len;
        int fixPart = 0;
        if (lastPartCount != 0) {
            fixPart = 1;
        }

        final String[] strs = new String[partCount + fixPart];
        for (int i = 0; i < partCount + fixPart; i++) {
            if (i == partCount + fixPart - 1 && lastPartCount != 0) {
                strs[i] = str.substring(i * len, i * len + lastPartCount);
            } else {
                strs[i] = str.substring(i * len, i * len + len);
            }
        }
        return strs;
    }

    /**
     * 切分字符串，不忽略大小写
     *
     * @param str 被切分的字符串
     * @param separator 分隔符字符串
     * @param limit 限制分片数
     * @param isTrim 是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(String str, String separator, int limit, boolean isTrim, boolean ignoreEmpty){
        return split(str, separator, limit, isTrim, ignoreEmpty, false);
    }

    /**
     * 切分字符串
     *
     * @param str 被切分的字符串
     * @param separator 分隔符字符串
     * @param limit 限制分片数
     * @param isTrim 是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @param ignoreCase 是否忽略大小写
     * @return 切分后的集合
     * @since 3.2.1
     */
    public static List<String> split(String str, String separator, int limit, boolean isTrim, boolean ignoreEmpty, boolean ignoreCase){
        if(isEmpty(str)){
            return new ArrayList<String>(0);
        }
        if(limit == 1){
            return addToList(new ArrayList<String>(1), str, isTrim, ignoreEmpty);
        }

        if(isEmpty(separator)){//分隔符为空时按照空白符切分
            return split(str, limit);
        }else if(separator.length() == 1){//分隔符只有一个字符长度时按照单分隔符切分
            return split(str, separator.charAt(0), limit, isTrim, ignoreEmpty, ignoreCase);
        }

        final ArrayList<String> list = new ArrayList<>();
        int len = str.length();
        int separatorLen = separator.length();
        int start = 0;
        int i = 0;
        while(i < len){
            i = indexOf(str, separator, start, ignoreCase);
            if(i > -1){
                addToList(list, str.substring(start, i), isTrim, ignoreEmpty);
                start = i + separatorLen;

                //检查是否超出范围（最大允许limit-1个，剩下一个留给末尾字符串）
                if(limit > 0 && list.size() > limit-2){
                    break;
                }
            }else{
                break;
            }
        }
        return addToList(list, str.substring(start, len), isTrim, ignoreEmpty);
    }

    /**
     * 切分字符串
     *
     * @param str 被切分的字符串
     * @param separator 分隔符字符
     * @param limit 限制分片数，-1不限制
     * @param isTrim 是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @param ignoreCase 是否忽略大小写
     * @return 切分后的集合
     * @since 3.2.1
     */
    public static List<String> split(String str, char separator, int limit, boolean isTrim, boolean ignoreEmpty, boolean ignoreCase){
        if(isEmpty(str)){
            return new ArrayList<String>(0);
        }
        if(limit == 1){
            return addToList(new ArrayList<String>(1), str, isTrim, ignoreEmpty);
        }

        final ArrayList<String> list = new ArrayList<>(limit > 0 ? limit : 16);
        int len = str.length();
        int start = 0;//切分后每个部分的起始
        for(int i = 0; i < len; i++){
            if(charEquals(separator, str.charAt(i), ignoreCase)){
                addToList(list, str.substring(start, i), isTrim, ignoreEmpty);
                start = i+1;//i+1同时将start与i保持一致

                //检查是否超出范围（最大允许limit-1个，剩下一个留给末尾字符串）
                if(limit > 0 && list.size() > limit-2){
                    break;
                }
            }
        }
        return addToList(list, str.substring(start, len), isTrim, ignoreEmpty);//收尾
    }

    /**
     * 使用空白符切分字符串<br>
     * 切分后的字符串两边不包含空白符，空串或空白符串并不做为元素之一
     *
     * @param str 被切分的字符串
     * @param limit 限制分片数
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(String str, int limit){
        if(isEmpty(str)){
            return new ArrayList<String>(0);
        }
        if(limit == 1){
            return addToList(new ArrayList<String>(1), str, true, true);
        }

        final ArrayList<String> list = new ArrayList<>();
        int len = str.length();
        int start = 0;//切分后每个部分的起始
        for(int i = 0; i < len; i++){
            if(CharUtil.isBlankChar(str.charAt(i))){
                addToList(list, str.substring(start, i), true, true);
                start = i+1;//i+1同时将start与i保持一致

                //检查是否超出范围（最大允许limit-1个，剩下一个留给末尾字符串）
                if(limit > 0 && list.size() > limit-2){
                    break;
                }
            }
        }
        return addToList(list, str.substring(start, len), true, true);//收尾
    }


    /**
     * 将字符串加入List中
     * @param list 列表
     * @param part 被加入的部分
     * @param isTrim 是否去除两端空白符
     * @param ignoreEmpty 是否略过空字符串（空字符串不做为一个元素）
     * @return 列表
     */
    private static List<String> addToList(List<String> list, String part, boolean isTrim, boolean ignoreEmpty){
        part = part.toString();
        if(isTrim){
            part = part.trim();
        }
        if(false == ignoreEmpty || false == part.isEmpty()){
            list.add(part);
        }
        return list;
    }

    /**
     * List转Array
     * @param list List
     * @return Array
     */
    private static String[] toArray(List<String> list){
        return list.toArray(new String[list.size()]);
    }

    /**
     * 比较两个字符是否相同
     *
     * @param c1 字符1
     * @param c2 字符2
     * @param ignoreCase 是否忽略大小写
     * @return 是否相同
     * @since 4.0.3
     */
    public static boolean charEquals(char c1, char c2, boolean ignoreCase) {
        if (ignoreCase) {
            return Character.toLowerCase(c1) == Character.toLowerCase(c2);
        }
        return c1 == c2;
    }

    /**
     * 反转字符串<br>
     * 例如：abcd =》dcba
     *
     * @param str 被反转的字符串
     * @return 反转后的字符串
     * @since 3.0.9
     */
    public static String reverse(String str) {
        return new String(ArrayUtil.reverse(str.toCharArray()));
    }

    /**
     * 是否以指定字符串结尾<br>
     * 如果给定的字符串和开头字符串都为null则返回true，否则任意一个值为null返回false
     *
     * @param str          被监测字符串
     * @param suffix       结尾字符串
     * @param isIgnoreCase 是否忽略大小写
     * @return 是否以指定字符串结尾
     */
    public static boolean endWith(CharSequence str, CharSequence suffix, boolean isIgnoreCase) {
        if (null == str || null == suffix) {
            return null == str && null == suffix;
        }

        if (isIgnoreCase) {
            return str.toString().toLowerCase().endsWith(suffix.toString().toLowerCase());
        } else {
            return str.toString().endsWith(suffix.toString());
        }
    }

    /**
     * 指定范围内查找字符串，忽略大小写
     *
     * @param str       字符串
     * @param searchStr 需要查找位置的字符串
     * @return 位置
     * @since 3.2.1
     */
    public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
        return lastIndexOfIgnoreCase(str, searchStr, str.length());
    }


    /**
     * 指定范围内查找字符串，忽略大小写<br>
     * fromIndex 为搜索起始位置，从后往前计数
     *
     * @param str       字符串
     * @param searchStr 需要查找位置的字符串
     * @param fromIndex 起始位置，从后往前计数
     * @return 位置
     * @since 3.2.1
     */
    public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr, int fromIndex) {
        return lastIndexOf(str, searchStr, fromIndex, true);
    }
    /**
     * 指定范围内查找字符串<br>
     *
     * @param str 字符串
     * @param searchStr 需要查找位置的字符串
     * @param fromIndex 起始位置，从后往前计数
     * @param ignoreCase 是否忽略大小写
     * @return 位置
     * @since 3.2.1
     */
    public static int lastIndexOf(final CharSequence str, final CharSequence searchStr, int fromIndex, boolean ignoreCase) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        fromIndex = Math.min(fromIndex, str.length());

        if (searchStr.length() == 0) {
            return fromIndex;
        }

        if (!ignoreCase) {
            // 不忽略大小写调用JDK方法
            return str.toString().lastIndexOf(searchStr.toString(), fromIndex);
        }

        for (int i = fromIndex; i > 0; i--) {
            if (isSubEquals(str, i, searchStr, 0, searchStr.length(), true)) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }


    /**
     * 如果给定字符串不是以给定的一个或多个字符串为结尾，则在尾部添加结尾字符串
     *
     * @param str          被检查的字符串
     * @param suffix       需要添加到结尾的字符串，不参与检查匹配
     * @param ignoreCase   检查结尾时是否忽略大小写
     * @param testSuffixes 需要额外检查的结尾字符串，如果以这些中的一个为结尾，则不再添加
     * @return 如果已经结尾，返回原字符串，否则返回添加结尾的字符串
     * @since 3.0.7
     */
    public static String appendIfMissing(CharSequence str, CharSequence suffix, boolean ignoreCase, CharSequence... testSuffixes) {
        if (str == null || isEmpty(suffix) || endWith(str, suffix, ignoreCase)) {
            return str(str);
        }
        if (ArrayUtil.isNotEmpty(testSuffixes)) {
            for (final CharSequence testSuffix : testSuffixes) {
                if (endWith(str, testSuffix, ignoreCase)) {
                    return str.toString();
                }
            }
        }
        return str.toString().concat(suffix.toString());
    }

    /**
     * 如果给定字符串不是以给定的一个或多个字符串为结尾，则在尾部添加结尾字符串<br>
     * 不忽略大小写
     *
     * @param str      被检查的字符串
     * @param suffix   需要添加到结尾的字符串
     * @param suffixes 需要额外检查的结尾字符串，如果以这些中的一个为结尾，则不再添加
     * @return 如果已经结尾，返回原字符串，否则返回添加结尾的字符串
     * @since 3.0.7
     */
    public static String appendIfMissing(CharSequence str, CharSequence suffix, CharSequence... suffixes) {
        return appendIfMissing(str, suffix, false, suffixes);
    }

    /**
     * 如果给定字符串不是以suffix结尾的，在尾部补充 suffix
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 补充后的字符串
     * @see #appendIfMissing(CharSequence, CharSequence, CharSequence...)
     */
    public static String addSuffixIfNot(CharSequence str, CharSequence suffix) {
        return appendIfMissing(str, suffix, suffix);
    }


    /**
     * 截取分隔字符串之前的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""）或者分隔字符串为null，返回原字符串<br>
     * 如果分隔字符串为空串""，则返回空串，如果分隔字符串未找到，返回原字符串，举例如下：
     *
     * <pre>
     * StrUtil.subBefore(null, *, false)      = null
     * StrUtil.subBefore("", *, false)        = ""
     * StrUtil.subBefore("abc", "a", false)   = ""
     * StrUtil.subBefore("abcba", "b", false) = "a"
     * StrUtil.subBefore("abc", "c", false)   = "ab"
     * StrUtil.subBefore("abc", "d", false)   = "abc"
     * StrUtil.subBefore("abc", "", false)    = ""
     * StrUtil.subBefore("abc", null, false)  = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 3.1.1
     */
    public static String subBefore(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (isEmpty(string) || separator == null) {
            return null == string ? null : string.toString();
        }

        final String str = string.toString();
        final String sep = separator.toString();
        if (sep.isEmpty()) {
            return EMPTY;
        }
        final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
        if (INDEX_NOT_FOUND == pos) {
            return str;
        }
        if (0 == pos) {
            return EMPTY;
        }
        return str.substring(0, pos);
    }

    /**
     * 截取分隔字符串之后的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""），返回原字符串<br>
     * 如果分隔字符串为空串（null或""），则返回空串，如果分隔字符串未找到，返回空串，举例如下：
     *
     * <pre>
     * StrUtil.subAfter(null, *, false)      = null
     * StrUtil.subAfter("", *, false)        = ""
     * StrUtil.subAfter(*, null, false)      = ""
     * StrUtil.subAfter("abc", "a", false)   = "bc"
     * StrUtil.subAfter("abcba", "b", false) = "cba"
     * StrUtil.subAfter("abc", "c", false)   = ""
     * StrUtil.subAfter("abc", "d", false)   = ""
     * StrUtil.subAfter("abc", "", false)    = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 3.1.1
     */
    public static String subAfter(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (isEmpty(string)) {
            return null == string ? null : EMPTY;
        }
        if (separator == null) {
            return EMPTY;
        }
        final String str = string.toString();
        final String sep = separator.toString();
        final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
        if (INDEX_NOT_FOUND == pos || (string.length() - 1) == pos) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }


    /**
     * 当给定字符串为null时，转换为Empty
     *
     * @param str 被转换的字符串
     * @return 转换后的字符串
     */
    public static String nullToEmpty(CharSequence str) {
        return nullToDefault(str, EMPTY);
    }

    /**
     * 如果字符串是 {@code null}，则返回指定默认字符串，否则返回字符串本身。
     *
     * <pre>
     * nullToDefault(null, &quot;default&quot;)  = &quot;default&quot;
     * nullToDefault(&quot;&quot;, &quot;default&quot;)    = &quot;&quot;
     * nullToDefault(&quot;  &quot;, &quot;default&quot;)  = &quot;  &quot;
     * nullToDefault(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     * @return 字符串本身或指定的默认字符串
     */
    public static String nullToDefault(CharSequence str, String defaultStr) {
        return (str == null) ? defaultStr : str.toString();
    }

    /**
     * 原字符串首字母大写并在其首部添加指定字符串 例如：str=name, preString=get =》 return getName
     *
     * @param str 被处理的字符串
     * @param preString 添加的首部
     * @return 处理后的字符串
     */
    public static String upperFirstAndAddPre(CharSequence str, String preString) {
        if (str == null || preString == null) {
            return null;
        }
        return preString + upperFirst(str);
    }

    /**
     * 大写首字母<br>
     * 例如：str = name, return Name
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String upperFirst(CharSequence str) {
        if (null == str) {
            return null;
        }
        if (str.length() > 0) {
            char firstChar = str.charAt(0);
            if (Character.isLowerCase(firstChar)) {
                return Character.toUpperCase(firstChar) + subSuf(str, 1);
            }
        }
        return str.toString();
    }
}
