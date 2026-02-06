package org.winterfell.misc.hutool.mini.io;

import org.winterfell.misc.hutool.mini.AssertUtil;
import org.winterfell.misc.hutool.mini.CharsetUtil;
import org.winterfell.misc.hutool.mini.HexUtil;
import org.winterfell.misc.hutool.mini.StringUtil;
import org.winterfell.misc.hutool.mini.convert.Convert;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/8
 */
public class IoUtil {

    /** 默认缓存大小 */
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    /** 默认中等缓存大小 */
    public static final int DEFAULT_MIDDLE_BUFFER_SIZE = 4096;
    /** 默认大缓存大小 */
    public static final int DEFAULT_LARGE_BUFFER_SIZE = 8192;

    /** 数据流末尾 */
    public static final int EOF = -1;


    /**
     * 获得{@link BufferedReader}<br>
     * 如果是{@link BufferedReader}强转返回，否则新建。如果提供的Reader为null返回null
     *
     * @param reader 普通Reader，如果为null返回null
     * @return {@link BufferedReader} or null
     * @since 3.0.9
     */
    public static BufferedReader getReader(Reader reader) {
        if (null == reader) {
            return null;
        }

        return (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
    }

    /**
     * 获得一个文件读取器，默认使用UTF-8编码
     *
     * @param in 输入流
     * @return BufferedReader对象
     * @since 5.1.6
     */
    public static BufferedReader getUtf8Reader(InputStream in) {
        return getReader(in, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 获得一个Reader
     *
     * @param in 输入流
     * @param charset 字符集
     * @return BufferedReader对象
     */
    public static BufferedReader getReader(InputStream in, Charset charset) {
        if (null == in) {
            return null;
        }

        InputStreamReader reader = null;
        if (null == charset) {
            reader = new InputStreamReader(in);
        } else {
            reader = new InputStreamReader(in, charset);
        }

        return new BufferedReader(reader);
    }

    public static FastByteArrayOutputStream read(InputStream in) throws IORuntimeException {
        final FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        copy(in, out);
        return out;
    }
    /**
     * 从流中读取内容，读取完毕后并不关闭流
     *
     * @param in 输入流，读取完毕后并不关闭流
     * @param charset 字符集
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public static String read(InputStream in, Charset charset) throws IORuntimeException {
        FastByteArrayOutputStream out = read(in);
        return null == charset ? out.toString() : out.toString(charset);
    }

    /**
     * 从Reader中读取String，读取完毕后并不关闭Reader
     *
     * @param reader Reader
     * @return String
     * @throws IORuntimeException IO异常
     */
    public static String read(Reader reader) throws IORuntimeException {
        final StringBuilder builder = StringUtil.builder();
        final CharBuffer buffer = CharBuffer.allocate(DEFAULT_BUFFER_SIZE);
        try {
            while (-1 != reader.read(buffer)) {
                builder.append(buffer.flip().toString());
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return builder.toString();
    }

    /**
     * 从流中读取bytes
     *
     * @param in {@link InputStream}
     * @return bytes
     * @throws IORuntimeException IO异常
     */
    public static byte[] readBytes(InputStream in) throws IORuntimeException {
        final FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }

    /**
     * 读取指定长度的byte数组，不关闭流
     *
     * @param in {@link InputStream}，为null返回null
     * @param length 长度，小于等于0返回空byte数组
     * @return bytes
     * @throws IORuntimeException IO异常
     */
    public static byte[] readBytes(InputStream in, int length) throws IORuntimeException {
        if (null == in) {
            return null;
        }
        if (length <= 0) {
            return new byte[0];
        }

        byte[] b = new byte[length];
        int readLength;
        try {
            readLength = in.read(b);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        if (readLength > 0 && readLength < length) {
            byte[] b2 = new byte[length];
            System.arraycopy(b, 0, b2, 0, readLength);
            return b2;
        } else {
            return b;
        }
    }

    /**
     * 将多部分内容写到流中，自动转换为字符串
     *
     * @param out 输出流
     * @param charset 写出的内容的字符集
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents 写入的内容，调用toString()方法，不包括不会自动换行
     * @throws IORuntimeException IO异常
     * @since 3.0.9
     */
    public static void write(OutputStream out, Charset charset, boolean isCloseOut, Object... contents) throws IORuntimeException {
        OutputStreamWriter osw = null;
        try {
            osw = getWriter(out, charset);
            for (Object content : contents) {
                if (content != null) {
                    osw.write(Convert.toStr(content, StringUtil.EMPTY));
                    osw.flush();
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (isCloseOut) {
                close(osw);
            }
        }
    }

    /**
     * 将byte[]写到流中
     *
     * @param out 输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param content 写入的内容
     * @throws IORuntimeException IO异常
     */
    public static void write(OutputStream out, boolean isCloseOut, byte[] content) throws IORuntimeException {
        try {
            out.write(content);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (isCloseOut) {
                close(out);
            }
        }
    }

    /**
     * 将多部分内容写到流中，自动转换为UTF-8字符串
     *
     * @param out 输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents 写入的内容，调用toString()方法，不包括不会自动换行
     * @throws IORuntimeException IO异常
     * @since 3.1.1
     */
    public static void writeUtf8(OutputStream out, boolean isCloseOut, Object... contents) throws IORuntimeException {
        write(out, CharsetUtil.CHARSET_UTF_8, isCloseOut, contents);
    }

    /**
     * 从流中读取前28个byte并转换为16进制，字母部分使用大写
     *
     * @param in {@link InputStream}
     * @return 16进制字符串
     * @throws IORuntimeException IO异常
     */
    public static String readHex28Upper(InputStream in) throws IORuntimeException {
        return readHex(in, 28, false);
    }

    /**
     * 读取16进制字符串
     *
     * @param in {@link InputStream}
     * @param length 长度
     * @param toLowerCase true 传换成小写格式 ， false 传换成大写格式
     * @return 16进制字符串
     * @throws IORuntimeException IO异常
     */
    public static String readHex(InputStream in, int length, boolean toLowerCase) throws IORuntimeException {
        return HexUtil.encodeHexStr(readBytes(in, length), toLowerCase);
    }

    /**
     * 获得一个Writer
     *
     * @param out 输入流
     * @param charset 字符集
     * @return OutputStreamWriter对象
     */
    public static OutputStreamWriter getWriter(OutputStream out, Charset charset) {
        if (null == out) {
            return null;
        }

        if (null == charset) {
            return new OutputStreamWriter(out);
        } else {
            return new OutputStreamWriter(out, charset);
        }
    }

    /**
     * 获得一个Writer，默认编码UTF-8
     *
     * @param out 输入流
     * @return OutputStreamWriter对象
     * @since 5.1.6
     */
    public static OutputStreamWriter getUtf8Writer(OutputStream out) {
        return getWriter(out, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 拷贝流，使用默认Buffer大小
     *
     * @param in 输入流
     * @param out 输出流
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(InputStream in, OutputStream out) throws IORuntimeException {
        return copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 拷贝流
     *
     * @param in 输入流
     * @param out 输出流
     * @param bufferSize 缓存大小
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize) throws IORuntimeException {
        return copy(in, out, bufferSize, null);
    }

    /**
     * 拷贝流
     *
     * @param in 输入流
     * @param out 输出流
     * @param bufferSize 缓存大小
     * @param streamProgress 进度条
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize, StreamProgress streamProgress) throws IORuntimeException {
        AssertUtil.notNull(in, "InputStream is null !");
        AssertUtil.notNull(out, "OutputStream is null !");
        if (bufferSize <= 0) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }

        byte[] buffer = new byte[bufferSize];
        long size = 0;
        if (null != streamProgress) {
            streamProgress.start();
        }
        try {
            for (int readSize = -1; (readSize = in.read(buffer)) != EOF;) {
                out.write(buffer, 0, readSize);
                size += readSize;
                out.flush();
                if (null != streamProgress) {
                    streamProgress.progress(size);
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        if (null != streamProgress) {
            streamProgress.finish();
        }
        return size;
    }


    /**
     * 关闭<br>
     * 关闭失败不会抛出异常
     *
     * @param closeable 被关闭的对象
     */
    public static void close(AutoCloseable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
            }
        }
    }

    /**
     * String 转为流
     *
     * @param content     内容
     * @param charsetName 编码
     * @return 字节流
     * @deprecated 请使用 {@link #toStream(String, Charset)}
     */
    @Deprecated
    public static ByteArrayInputStream toStream(String content, String charsetName) {
        return toStream(content, CharsetUtil.charset(charsetName));
    }

    /**
     * String 转为流
     *
     * @param content 内容
     * @param charset 编码
     * @return 字节流
     */
    public static ByteArrayInputStream toStream(String content, Charset charset) {
        if (content == null) {
            return null;
        }
        return toStream(StringUtil.bytes(content, charset));
    }

    /**
     * 文件转为流
     *
     * @param file 文件
     * @return {@link FileInputStream}
     */
    public static FileInputStream toStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * byte[] 转为{@link ByteArrayInputStream}
     *
     * @param content 内容bytes
     * @return 字节流
     * @since 4.1.8
     */
    public static ByteArrayInputStream toStream(byte[] content) {
        if (content == null) {
            return null;
        }
        return new ByteArrayInputStream(content);
    }

    /**
     * {@link ByteArrayOutputStream}转为{@link ByteArrayInputStream}
     *
     * @param out {@link ByteArrayOutputStream}
     * @return 字节流
     * @since 5.3.6
     */
    public static ByteArrayInputStream toStream(ByteArrayOutputStream out) {
        if (out == null) {
            return null;
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * 转换为{@link BufferedInputStream}
     *
     * @param in {@link InputStream}
     * @return {@link BufferedInputStream}
     * @since 4.0.10
     */
    public static BufferedInputStream toBuffered(InputStream in) {
        return (in instanceof BufferedInputStream) ? (BufferedInputStream) in : new BufferedInputStream(in);
    }

    /**
     * 转换为{@link BufferedOutputStream}
     *
     * @param out {@link OutputStream}
     * @return {@link BufferedOutputStream}
     * @since 4.0.10
     */
    public static BufferedOutputStream toBuffered(OutputStream out) {
        return (out instanceof BufferedOutputStream) ? (BufferedOutputStream) out : new BufferedOutputStream(out);
    }

    /**
     * 将{@link InputStream}转换为支持mark标记的流<br>
     * 若原流支持mark标记，则返回原流，否则使用{@link BufferedInputStream} 包装之
     *
     * @param in 流
     * @return {@link InputStream}
     * @since 4.0.9
     */
    public static InputStream toMarkSupportStream(InputStream in) {
        if (null == in) {
            return null;
        }
        if (!in.markSupported()) {
            return new BufferedInputStream(in);
        }
        return in;
    }

}
