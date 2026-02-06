package org.winterfell.misc.hutool.mini.io;

import org.winterfell.misc.hutool.mini.*;
import org.winterfell.misc.hutool.mini.thread.ThreadUtil;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/8
 */
public final class FileUtil {


    /** 类Unix路径分隔符 */
    private static final char UNIX_SEPARATOR = CharUtil.SLASH;
    /** Windows路径分隔符 */
    private static final char WINDOWS_SEPARATOR = CharUtil.BACKSLASH;
    /** Windows下文件名中的无效字符 */
    private static Pattern FILE_NAME_INVALID_PATTERN_WIN = Pattern.compile("[\\\\/:*?\"<>|]");

    /** Class文件扩展名 */
    public static final String CLASS_EXT = ".class";
    /** Jar文件扩展名 */
    public static final String JAR_FILE_EXT = ".jar";
    /** 在Jar中的路径jar的扩展名形式 */
    public static final String JAR_PATH_EXT = ".jar!";
    /** 当Path为文件形式时, path会加入一个表示文件的前缀 */
    public static final String PATH_FILE_PRE = URLUtil.FILE_URL_PREFIX;

    /**
     * 是否为Windows环境
     *
     * @return 是否为Windows环境
     * @since 3.0.9
     */
    public static boolean isWindows() {
        return WINDOWS_SEPARATOR == File.separatorChar;
    }



    /**
     * 在默认临时文件目录下创建临时文件，创建后的文件名为 prefix[Randon].suffix。
     * 默认临时文件目录由系统属性 {@code java.io.tmpdir} 指定。
     * 在 UNIX 系统上，此属性的默认值通常是 {@code "tmp"} 或 {@code "vartmp"}；
     * 在 Microsoft Windows 系统上，它通常是 {@code "C:\\WINNT\\TEMP"}。
     * 调用 Java 虚拟机时，可以为该系统属性赋予不同的值，但不保证对该属性的编程更改对该方法使用的临时目录有任何影响。
     * @param suffix    后缀，如果null则使用默认.tmp
     * @param isReCreat 是否重新创建文件（删掉原来的，创建新的）
     * @return 临时文件
     * @throws IORuntimeException IO异常
     * @since 5.7.22
     */
    public static File createTempFile(String suffix, boolean isReCreat) throws IORuntimeException {
        return createTempFile("hutool", suffix, null, isReCreat);
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 prefix[Randon].suffix From com.jodd.io.FileUtil
     *
     * @param prefix    前缀，至少3个字符
     * @param suffix    后缀，如果null则使用默认.tmp
     * @param dir       临时文件创建的所在目录
     * @param isReCreat 是否重新创建文件（删掉原来的，创建新的）
     * @return 临时文件
     * @throws IORuntimeException IO异常
     */
    public static File createTempFile(String prefix, String suffix, File dir, boolean isReCreat) throws IORuntimeException {
        int exceptionsCount = 0;
        while (true) {
            try {
                File file = File.createTempFile(prefix, suffix, mkdir(dir)).getCanonicalFile();
                if (isReCreat) {
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                }
                return file;
            } catch (IOException ioex) { // fixes java.io.WinNTFileSystem.createFileExclusively access denied
                if (++exceptionsCount >= 50) {
                    throw new IORuntimeException(ioex);
                }
            }
        }
    }

    /**
     * 复制文件或目录<br>
     * 情况如下：
     *
     * <pre>
     * 1、src和dest都为目录，则将src目录及其目录下所有文件目录拷贝到dest下
     * 2、src和dest都为文件，直接复制，名字为dest
     * 3、src为文件，dest为目录，将src拷贝到dest目录下
     * </pre>
     *
     * @param src 源文件
     * @param dest 目标文件或目录，目标不存在会自动创建（目录、文件都创建）
     * @param isOverride 是否覆盖目标文件
     * @return 目标目录或文件
     * @throws IORuntimeException IO异常
     */
    public static File copy(File src, File dest, boolean isOverride) throws IORuntimeException {
        return FileCopier.create(src, dest).setOverride(isOverride).copy();
    }

    /**
     * 创建文件夹，会递归自动创建其不存在的父文件夹，如果存在直接返回此文件夹<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型<br>
     *
     * @param dir 目录
     * @return 创建的目录
     */
    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (!dir.exists()) {
            mkdirsSafely(dir, 5, 1);
        }
        return dir;
    }

    /**
     * 安全地级联创建目录 (确保并发环境下能创建成功)
     *
     * <pre>
     *     并发环境下，假设 test 目录不存在，如果线程A mkdirs "test/A" 目录，线程B mkdirs "test/B"目录，
     *     其中一个线程可能会失败，进而导致以下代码抛出 FileNotFoundException 异常
     *
     *     file.getParentFile().mkdirs(); // 父目录正在被另一个线程创建中，返回 false
     *     file.createNewFile(); // 抛出 IO 异常，因为该线程无法感知到父目录已被创建
     * </pre>
     *
     * @param dir 待创建的目录
     * @param tryCount 最大尝试次数
     * @param sleepMillis 线程等待的毫秒数
     * @return true表示创建成功，false表示创建失败
     * @since 5.7.21
     * @author z8g
     */
    public static boolean mkdirsSafely(File dir, int tryCount, long sleepMillis) {
        if (dir == null) {
            return false;
        }
        if (dir.isDirectory()) {
            return true;
        }
        for (int i = 1; i <= tryCount; i++) { // 高并发场景下，可以看到 i 处于 1 ~ 3 之间
            // 如果文件已存在，也会返回 false，所以该值不能作为是否能创建的依据，因此不对其进行处理
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            if (dir.exists()) {
                return true;
            }
            ThreadUtil.sleep(sleepMillis);
        }
        return dir.exists();
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param file 文件或目录
     * @return 父目录
     */
    public static File mkParentDirs(File file) {
        final File parentFile = file.getParentFile();
        if (null != parentFile && false == parentFile.exists()) {
            parentFile.mkdirs();
        }
        return parentFile;
    }

    /**
     * 创建父文件夹，如果存在直接返回此文件夹
     *
     * @param path 文件夹路径，使用POSIX格式，无论哪个平台
     * @return 创建的目录
     */
    public static File mkParentDirs(String path) {
        if (path == null) {
            return null;
        }
        return mkParentDirs(file(path));
    }

    /**
     * 创建File对象
     *
     * @param uri 文件URI
     * @return File
     */
    public static File file(URI uri) {
        if (uri == null) {
            throw new NullPointerException("File uri is null!");
        }
        return new File(uri);
    }

    /**
     * 创建File对象
     *
     * @param url 文件URL
     * @return File
     */
    public static File file(URL url) {
        return new File(URLUtil.toURI(url));
    }

    /**
     * 创建File对象，自动识别相对或绝对路径，相对路径将自动从ClassPath下寻找
     *
     * @param path 文件路径
     * @return File
     */
    public static File file(String path) {
        if (StringUtil.isBlank(path)) {
            throw new NullPointerException("File path is blank!");
        }
        return new File(getAbsolutePath(path));
    }

    /**
     * 创建File对象
     *
     * @param parent 父目录
     * @param path 文件路径
     * @return File
     */
    public static File file(String parent, String path) {
        if (StringUtil.isBlank(path)) {
            throw new NullPointerException("File path is blank!");
        }
        return new File(parent, path);
    }

    /**
     * 创建File对象
     *
     * @param parent 父文件对象
     * @param path 文件路径
     * @return File
     */
    public static File file(File parent, String path) {
        if (StringUtil.isBlank(path)) {
            throw new NullPointerException("File path is blank!");
        }
        return new File(parent, path);
    }


    /**
     * 获取临时文件路径（绝对路径）
     *
     * @return 临时文件路径
     * @since 4.0.6
     */
    public static String getTmpDirPath() {
        return System.getProperty("java.io.tmpdir");
    }


    /**
     * 获取绝对路径，相对于ClassPath的目录<br>
     * 如果给定就是绝对路径，则返回原路径，原路径把所有\替换为/<br>
     * 兼容Spring风格的路径表示，例如：classpath:config/example.setting也会被识别后转换
     *
     * @param path 相对路径
     * @return 绝对路径
     */
    public static String getAbsolutePath(String path) {
        return getAbsolutePath(path, null);
    }

    /**
     * 获取绝对路径<br>
     * 此方法不会判定给定路径是否有效（文件或目录存在）
     *
     * @param path 相对路径
     * @param baseClass 相对路径所相对的类
     * @return 绝对路径
     */
    public static String getAbsolutePath(String path, Class<?> baseClass) {
        String normalPath;
        if (path == null) {
            normalPath = StringUtil.EMPTY;
        } else {
            normalPath = normalize(path);
            if (isAbsolutePath(normalPath)) {
                // 给定的路径已经是绝对路径了
                return normalPath;
            }
        }

        // 相对于ClassPath路径
        final URL url = ResourceUtil.getResource(normalPath, baseClass);
        if (null != url) {
            // 对于jar中文件包含file:前缀，需要去掉此类前缀，在此做标准化，since 3.0.8 解决中文或空格路径被编码的问题
            return FileUtil.normalize(URLUtil.getDecodedPath(url));
        }

        // 如果资源不存在，则返回一个拼接的资源绝对路径
        final String classPath = ClassUtil.getClassPath();
        if (null == classPath) {
            throw new NullPointerException("ClassPath is null !");
        }

        // 资源不存在的情况下使用标准化路径有问题，使用原始路径拼接后标准化路径
        return normalize(classPath.concat(path));
    }

    /**
     * 给定路径已经是绝对路径<br>
     * 此方法并没有针对路径做标准化，建议先执行{@link #normalize(String)}方法标准化路径后判断
     *
     * @param path 需要检查的Path
     * @return 是否已经是绝对路径
     */
    public static boolean isAbsolutePath(String path) {
        if (StringUtil.isEmpty(path)) {
            return false;
        }

        if (StringUtil.C_SLASH == path.charAt(0) || path.matches("^[a-zA-Z]:[/\\\\].*")) {
            // 给定的路径已经是绝对路径了
            return true;
        }
        return false;
    }

    /**
     * 修复路径<br>
     * 如果原路径尾部有分隔符，则保留为标准分隔符（/），否则不保留
     * <ol>
     * <li>1. 统一用 /</li>
     * <li>2. 多个 / 转换为一个 /</li>
     * <li>3. 去除两边空格</li>
     * <li>4. .. 和 . 转换为绝对路径，当..多于已有路径时，直接返回根路径</li>
     * </ol>
     *
     * 栗子：
     *
     * <pre>
     * "/foo//" =》 "/foo/"
     * "/foo/./" =》 "/foo/"
     * "/foo/../bar" =》 "/bar"
     * "/foo/../bar/" =》 "/bar/"
     * "/foo/../bar/../baz" =》 "/baz"
     * "/../" =》 "/"
     * "foo/bar/.." =》 "foo"
     * "foo/../bar" =》 "bar"
     * "foo/../../bar" =》 "bar"
     * "//server/foo/../bar" =》 "/server/bar"
     * "//server/../bar" =》 "/bar"
     * "C:\\foo\\..\\bar" =》 "C:/bar"
     * "C:\\..\\bar" =》 "C:/bar"
     * "~/foo/../bar/" =》 "~/bar/"
     * "~/../bar" =》 "bar"
     * </pre>
     *
     * @param path 原路径
     * @return 修复后的路径
     */
    public static String normalize(String path) {
        if (path == null) {
            return null;
        }

        // 兼容Spring风格的ClassPath路径，去除前缀，不区分大小写
        String pathToUse = StringUtil.removePrefixIgnoreCase(path, "classpath:");
        // 去除file:前缀
        pathToUse = StringUtil.removePrefixIgnoreCase(pathToUse, "file:");
        // 统一使用斜杠
        pathToUse = pathToUse.replaceAll("[/\\\\]{1,}", "/").trim();

        int prefixIndex = pathToUse.indexOf(StringUtil.COLON);
        String prefix = "";
        if (prefixIndex > -1) {
            // 可能Windows风格路径
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (StringUtil.startWith(prefix, StringUtil.C_SLASH)) {
                // 去除类似于/C:这类路径开头的斜杠
                prefix = prefix.substring(1);
            }
            if (false == prefix.contains("/")) {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            } else {
                // 如果前缀中包含/,说明非Windows风格path
                prefix = StringUtil.EMPTY;
            }
        }
        if (pathToUse.startsWith(StringUtil.SLASH)) {
            prefix += StringUtil.SLASH;
            pathToUse = pathToUse.substring(1);
        }

        List<String> pathList = StringUtil.split(pathToUse, StringUtil.C_SLASH);
        List<String> pathElements = new LinkedList<String>();
        int tops = 0;

        String element;
        for (int i = pathList.size() - 1; i >= 0; i--) {
            element = pathList.get(i);
            if (StringUtil.DOT.equals(element)) {
                // 当前目录，丢弃
            } else if (StringUtil.DOUBLE_DOT.equals(element)) {
                tops++;
            } else {
                if (tops > 0) {
                    // 有上级目录标记时按照个数依次跳过
                    tops--;
                } else {
                    // Normal path element found.
                    pathElements.add(0, element);
                }
            }
        }
        return prefix +  StringUtil.JOINER_SLASH.join(pathElements);
    }


    /**
     * 获取标准的绝对路径
     *
     * @param file 文件
     * @return 绝对路径
     */
    public static String getAbsolutePath(File file) {
        if (file == null) {
            return null;
        }

        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

    /**
     * 获取临时文件目录
     *
     * @return 临时文件目录
     * @since 4.0.6
     */
    public static File getTmpDir() {
        return file(getTmpDirPath());
    }


    /**
     * 读取文件所有数据<br>
     * 文件的长度不能超过Integer.MAX_VALUE
     *
     * @param file 文件
     * @return 字节码
     * @throws IORuntimeException IO异常
     */
    public static byte[] readBytes(File file) throws IORuntimeException {
        long len = file.length();
        if (len >= Integer.MAX_VALUE) {
            throw new IORuntimeException("File is larger then max array size");
        }

        byte[] bytes = new byte[(int) len];
        FileInputStream in = null;
        int readLength;
        try {
            in = new FileInputStream(file);
            readLength = in.read(bytes);
            if(readLength < len){
                throw new IOException(StringUtil.format("File length is [{}] but read [{}]!", len, readLength));
            }
        } catch (Exception e) {
            throw new IORuntimeException(e);
        } finally {
            IoUtil.close(in);
        }

        return bytes;
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param fullFilePath 文件的全路径，使用POSIX风格
     * @return 文件，若路径为null，返回null
     * @throws IORuntimeException IO异常
     */
    public static File touch(String fullFilePath) throws IORuntimeException {
        if (fullFilePath == null) {
            return null;
        }
        return touch(file(fullFilePath));
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param file 文件对象
     * @return 文件，若路径为null，返回null
     * @throws IORuntimeException IO异常
     */
    public static File touch(File file) throws IORuntimeException {
        if (null == file) {
            return null;
        }
        if (!file.exists()) {
            mkParentDirs(file);
            try {
                file.createNewFile();
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }
        return file;
    }

    /**
     * 获得一个输出流对象
     *
     * @param file 文件
     * @return 输出流对象
     * @throws IORuntimeException IO异常
     */
    public static BufferedOutputStream getOutputStream(File file) throws IORuntimeException {
        try {
            return new BufferedOutputStream(Files.newOutputStream(touch(file).toPath()));
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 获得一个输出流对象
     *
     * @param path 输出到的文件路径，绝对路径
     * @return 输出流对象
     * @throws IORuntimeException IO异常
     */
    public static BufferedOutputStream getOutputStream(String path) throws IORuntimeException {
        return getOutputStream(touch(path));
    }

    /**
     * 获得输入流
     *
     * @param path Path
     * @return 输入流
     * @throws IORuntimeException 文件未找到
     * @since 4.0.0
     */
    public static BufferedInputStream getInputStream(Path path) throws IORuntimeException {
        try {
            return new BufferedInputStream(Files.newInputStream(path));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 获得输入流
     *
     * @param file 文件
     * @return 输入流
     * @throws IORuntimeException 文件未找到
     */
    public static BufferedInputStream getInputStream(File file) throws IORuntimeException {
        return new BufferedInputStream(IoUtil.toStream(file));
    }

    /**
     * 获得输入流
     *
     * @param path 文件路径
     * @return 输入流
     * @throws IORuntimeException 文件未找到
     */
    public static BufferedInputStream getInputStream(String path) throws IORuntimeException {
        return getInputStream(file(path));
    }

    /**
     * 获取文件扩展名，扩展名不带“.”
     *
     * @param file 文件
     * @return 扩展名
     */
    public static String extName(File file) {
        if (null == file) {
            return null;
        }
        if (file.isDirectory()) {
            return null;
        }
        return extName(file.getName());
    }

    /**
     * 获得文件的扩展名，扩展名不带“.”
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String extName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int index = fileName.lastIndexOf(StringUtil.DOT);
        if (index == -1) {
            return StringUtil.EMPTY;
        } else {
            String ext = fileName.substring(index + 1);
            // 扩展名中不能包含路径相关的符号
            return (ext.contains(String.valueOf(UNIX_SEPARATOR)) || ext.contains(String.valueOf(WINDOWS_SEPARATOR))) ? StringUtil.EMPTY : ext;
        }
    }

    /**
     * 检查两个文件是否是同一个文件<br>
     * 所谓文件相同，是指File对象是否指向同一个文件或文件夹
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     * @throws IORuntimeException IO异常
     * @see Files#isSameFile(Path, Path)
     */
    public static boolean equals(File file1, File file2) throws IORuntimeException {
        AssertUtil.notNull(file1);
        AssertUtil.notNull(file2);
        if (!file1.exists() || !file2.exists()) {
            // 两个文件都不存在判断其路径是否相同
            if (!file1.exists() && !file2.exists() && pathEquals(file1, file2)) {
                return true;
            }
            // 对于一个存在一个不存在的情况，一定不相同
            return false;
        }
        try {
            return Files.isSameFile(file1.toPath(), file2.toPath());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 文件路径是否相同<br>
     * 取两个文件的绝对路径比较，在Windows下忽略大小写，在Linux下不忽略。
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 文件路径是否相同
     * @since 3.0.9
     */
    public static boolean pathEquals(File file1, File file2) {
        if (isWindows()) {
            // Windows环境
            try {
                if (StringUtil.equalsIgnoreCase(file1.getCanonicalPath(), file2.getCanonicalPath())) {
                    return true;
                }
            } catch (Exception e) {
                if (StringUtil.equalsIgnoreCase(file1.getAbsolutePath(), file2.getAbsolutePath())) {
                    return true;
                }
            }
        } else {
            // 类Unix环境
            try {
                if (StringUtil.equals(file1.getCanonicalPath(), file2.getCanonicalPath())) {
                    return true;
                }
            } catch (Exception e) {
                if (StringUtil.equals(file1.getAbsolutePath(), file2.getAbsolutePath())) {
                    return true;
                }
            }
        }
        return false;
    }



}
