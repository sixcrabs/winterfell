package org.winterfell.misc.hutool.mini.codec;

import java.nio.charset.Charset;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/8
 */
public final class Base64Util {

    /**
     * base64编码
     *
     * @param source 被编码的base64字符串
     * @return 被加密后的字符串
     */
    public static String encode(byte[] source) {
        return Base64Encoder.encode(source);
    }

    /**
     * 编码为Base64，非URL安全的
     *
     * @param arr 被编码的数组
     * @param lineSep 在76个char之后是CRLF还是EOF
     * @return 编码后的bytes
     */
    public static byte[] encode(byte[] arr, boolean lineSep) {
        return Base64Encoder.encode(arr, lineSep);
    }

    /**
     * 编码为Base64，URL安全的
     *
     * @param arr 被编码的数组
     * @param lineSep 在76个char之后是CRLF还是EOF
     * @return 编码后的bytes
     * @since 3.0.6
     */
    public static byte[] encodeUrlSafe(byte[] arr, boolean lineSep) {
        return Base64Encoder.encodeUrlSafe(arr, lineSep);
    }

    /**
     * base64编码
     *
     * @param source 被编码的base64字符串
     * @return 被加密后的字符串
     */
    public static String encode(String source) {
        return Base64Encoder.encode(source);
    }

    /**
     * base64编码，URL安全
     *
     * @param source 被编码的base64字符串
     * @return 被加密后的字符串
     * @since 3.0.6
     */
    public static String encodeUrlSafe(String source) {
        return Base64Encoder.encodeUrlSafe(source);
    }

    /**
     * base64编码,URL安全的
     *
     * @param source 被编码的base64字符串
     * @return 被加密后的字符串
     * @since 3.0.6
     */
    public static String encodeUrlSafe(byte[] source) {
        if (source == null) {
            return null;
        }
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(source);
    }


    // -------------------------------------------------------------------- decode
    /**
     * base64解码
     *
     * @param source 被解码的base64字符串
     * @return 被加密后的字符串
     */
    public static String decodeStr(String source) {
        return Base64Decoder.decodeStr(source);
    }

    /**
     * base64解码
     *
     * @param source 被解码的base64字符串
     * @param charset 字符集
     * @return 被加密后的字符串
     */
    public static String decodeStr(String source, String charset) {
        return Base64Decoder.decodeStr(source, charset);
    }

    /**
     * base64解码
     *
     * @param source 被解码的base64字符串
     * @param charset 字符集
     * @return 被加密后的字符串
     */
    public static String decodeStr(String source, Charset charset) {
        return Base64Decoder.decodeStr(source, charset);
    }

//    /**
//     * base64解码
//     *
//     * @param base64 被解码的base64字符串
//     * @param destFile 目标文件
//     * @return 目标文件
//     * @since 4.0.9
//     */
//    public static File decodeToFile(String base64, File destFile) {
//        return FileUtil.writeBytes(Base64Decoder.decode(base64), destFile);
//    }
//
//    /**
//     * base64解码
//     *
//     * @param base64 被解码的base64字符串
//     * @param out 写出到的流
//     * @param isCloseOut 是否关闭输出流
//     * @since 4.0.9
//     */
//    public static void decodeToStream(String base64, OutputStream out, boolean isCloseOut) {
//        IoUtil.write(out, isCloseOut, Base64Decoder.decode(base64));
//    }

    /**
     * base64解码
     *
     * @param base64 被解码的base64字符串
     * @return 被加密后的字符串
     */
    public static byte[] decode(String base64) {
        return Base64Decoder.decode(base64);
    }

    /**
     * base64解码
     *
     * @param source 被解码的base64字符串
     * @param charset 字符集
     * @return 被加密后的字符串
     */
    public static byte[] decode(String source, String charset) {
        return Base64Decoder.decode(source, charset);
    }

    /**
     * base64解码
     *
     * @param source 被解码的base64字符串
     * @param charset 字符集
     * @return 被加密后的字符串
     */
    public static byte[] decode(String source, Charset charset) {
        return Base64Decoder.decode(source, charset);
    }

    /**
     * 解码Base64
     *
     * @param in 输入
     * @return 解码后的bytes
     */
    public static byte[] decode(byte[] in) {
        return Base64Decoder.decode(in);
    }
}
