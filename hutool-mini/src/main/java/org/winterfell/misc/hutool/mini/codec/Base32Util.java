package org.winterfell.misc.hutool.mini.codec;

import org.winterfell.misc.hutool.mini.CharsetUtil;
import org.winterfell.misc.hutool.mini.StringUtil;

import java.nio.charset.Charset;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/5/20
 */
public class Base32Util {

    //----------------------------------------------------------------------------------------- encode

    /**
     * 编码
     *
     * @param bytes 数据
     * @return base32
     */
    public static String encode(final byte[] bytes) {
        return Base32Codec.INSTANCE.encode(bytes);
    }

    /**
     * base32编码
     *
     * @param source 被编码的base32字符串
     * @return 被加密后的字符串
     */
    public static String encode(String source) {
        return encode(source, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * base32编码
     *
     * @param source  被编码的base32字符串
     * @param charset 字符集
     * @return 被加密后的字符串
     */
    public static String encode(String source, Charset charset) {
        return encode(StringUtil.bytes(source, charset));
    }

    /**
     * 编码
     *
     * @param bytes 数据（Hex模式）
     * @return base32
     */
    public static String encodeHex(final byte[] bytes) {
        return Base32Codec.INSTANCE.encode(bytes, true);
    }

    /**
     * base32编码（Hex模式）
     *
     * @param source 被编码的base32字符串
     * @return 被加密后的字符串
     */
    public static String encodeHex(String source) {
        return encodeHex(source, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * base32编码（Hex模式）
     *
     * @param source  被编码的base32字符串
     * @param charset 字符集
     * @return 被加密后的字符串
     */
    public static String encodeHex(String source, Charset charset) {
        return encodeHex(StringUtil.bytes(source, charset));
    }

    //----------------------------------------------------------------------------------------- decode

    /**
     * 解码
     *
     * @param base32 base32编码
     * @return 数据
     */
    public static byte[] decode(String base32) {
        return Base32Codec.INSTANCE.decode(base32);
    }

    /**
     * base32解码
     *
     * @param source 被解码的base32字符串
     * @return 被加密后的字符串
     */
    public static String decodeStr(String source) {
        return decodeStr(source, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * base32解码
     *
     * @param source  被解码的base32字符串
     * @param charset 字符集
     * @return 被加密后的字符串
     */
    public static String decodeStr(String source, Charset charset) {
        return StringUtil.str(decode(source), charset);
    }

    /**
     * 解码
     *
     * @param base32 base32编码
     * @return 数据
     */
    public static byte[] decodeHex(String base32) {
        return Base32Codec.INSTANCE.decode(base32, true);
    }

    /**
     * base32解码
     *
     * @param source 被解码的base32字符串
     * @return 被加密后的字符串
     */
    public static String decodeStrHex(String source) {
        return decodeStrHex(source, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * base32解码
     *
     * @param source  被解码的base32字符串
     * @param charset 字符集
     * @return 被加密后的字符串
     */
    public static String decodeStrHex(String source, Charset charset) {
        return StringUtil.str(decodeHex(source), charset);
    }
}
