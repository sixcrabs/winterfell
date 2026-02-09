package org.winterfell.misc.hutool.mini;

import org.winterfell.misc.hutool.mini.io.FileUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class CharsetUtil {

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
     * 转换为Charset对象
     * @param charsetName 字符集，为空则返回默认字符集
     * @return Charset
     * @throws UnsupportedCharsetException 编码不支持
     */
    public static Charset charset(String charsetName) throws UnsupportedCharsetException{
        return StringUtil.isBlank(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
    }

    /**
     * 系统字符集编码，如果是Windows，则默认为GBK编码，否则取 {@link CharsetUtil#defaultCharsetName()}
     *
     * @return 系统字符集编码
     * @see CharsetUtil#defaultCharsetName()
     * @since 3.1.2
     */
    public static String systemCharsetName() {
        return systemCharset().name();
    }
    /**
     * 系统字符集编码，如果是Windows，则默认为GBK编码，否则取 {@link CharsetUtil#defaultCharsetName()}
     *
     * @return 系统字符集编码
     * @see CharsetUtil#defaultCharsetName()
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
}
