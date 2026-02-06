package org.winterfell.minio.starter;

import com.google.common.base.Splitter;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FastByteArrayOutputStream;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.winterfell.minio.starter.MinioUtil.extName;

/**
 * <p>
 * high level client for Minio
 * - 上传对象
 * - 获取对象
 * - 重名对象处理
 * - bucket 检测
 * - 请求对象 range
 * - ....
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/12/8
 */
public class MinioHighLevelClient {

    public static final Splitter PATH_SPLITTER = Splitter.on("/");

    public static final Logger LOG = LoggerFactory.getLogger(MinioHighLevelClient.class);

    /**
     * 与 minio 通信
     */
    private MinioClient client;


    public static Builder builder() {
        return new Builder();
    }

    private MinioHighLevelClient() {

    }

    private MinioHighLevelClient(Builder builder) {
        // TODO
        this.client = builder.minioClient;

    }

    public MinioClient getClient() {
        return client;
    }

    /**
     * bucket 是否存在
     *
     * @param bucketName
     * @return
     * @throws MinioHighLevelClientException
     */
    public boolean bucketExist(String bucketName) throws MinioHighLevelClientException {
        try {
            return client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new MinioHighLevelClientException(e.getLocalizedMessage());
        }
    }

    /**
     * 创建 bucket
     *
     * @param bucketName
     * @param lockObject
     * @return
     * @throws MinioHighLevelClientException
     */
    public boolean createBucket(String bucketName, boolean lockObject) throws MinioHighLevelClientException {
        try {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).objectLock(lockObject).build());
            return true;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new MinioHighLevelClientException(e.getLocalizedMessage());
        }
    }

    /**
     * remove bucket
     *
     * @param bucketName
     * @return
     * @throws MinioHighLevelClientException
     */
    public boolean removeBucket(String bucketName) throws MinioHighLevelClientException {
        try {
            client.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            return true;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new MinioHighLevelClientException(e.getLocalizedMessage());
        }
    }

    /**
     * get tags of bucket
     *
     * @param bucketName
     * @return
     * @throws MinioHighLevelClientException
     */
    public Map<String, String> getBucketTags(String bucketName) throws MinioHighLevelClientException {
        try {
            Tags tags = client.getBucketTags(GetBucketTagsArgs.builder().bucket(bucketName).build());
            if (tags != null) {
                return tags.get();
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new MinioHighLevelClientException(e.getLocalizedMessage());
        }
        return Collections.emptyMap();
    }

    /**
     * get tags of object
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws MinioHighLevelClientException
     */
    public Map<String, String> getObjectTags(String bucketName, String objectName) throws MinioHighLevelClientException {
        try {
            Tags tags = client.getObjectTags(GetObjectTagsArgs.builder().bucket(bucketName).object(objectName).build());
            if (tags != null) {
                return tags.get();
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new MinioHighLevelClientException(e.getLocalizedMessage());
        }
        return Collections.emptyMap();
    }

    /**
     * TESTME:
     * get object by full path
     *
     * @param objectFullPath
     * @return
     * @throws MinioHighLevelClientException
     */
    public MinioObject getObject(String objectFullPath) throws MinioHighLevelClientException {
        List<String> list = PATH_SPLITTER.splitToList(objectFullPath);
        return getObject(list.get(0), list.stream().skip(1L).collect(Collectors.joining("/")));
    }

    /**
     * get object
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws MinioHighLevelClientException
     */
    public MinioObject getObject(String bucketName, String objectName) {
        try {
            return getObject(bucketName, objectName, null, null);
        } catch (MinioHighLevelClientException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 支持 range 获取对象
     *
     * @param bucketName
     * @param objectName
     * @param offset
     * @param length
     * @return
     * @throws MinioHighLevelClientException
     */
    public MinioObject getObject(String bucketName, String objectName, Long offset, Long length) throws MinioHighLevelClientException {
        ObjectMetadata metadata = getObjectMetadata(bucketName, objectName);
        GetObjectArgs.Builder builder = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName);
        if (offset != null && offset > 0 && length != null) {
            builder.length(length).offset(offset);
        }
        try (GetObjectResponse response = client.getObject(builder.build())) {
            byte[] buf = new byte[1024];
            int len;
            byte[] bytes;
            try (FastByteArrayOutputStream byteArrayOutputStream = new FastByteArrayOutputStream()) {
                while ((len = response.read(buf)) != -1) {
                    byteArrayOutputStream.write(buf, 0, len);
                }
                byteArrayOutputStream.flush();
                bytes = byteArrayOutputStream.toByteArray();
            }
            return new MinioObject().setContent(bytes).setMetadata(metadata);

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new MinioHighLevelClientException(e.getLocalizedMessage());
        }
    }


    /**
     * 获取 object 元数据信息
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws MinioHighLevelClientException
     */
    public ObjectMetadata getObjectMetadata(String bucketName, String objectName) throws MinioHighLevelClientException {
        try {
            StatObjectResponse response = client.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return new ObjectMetadata()
                    .setContentType(response.contentType())
                    .setObjectName(response.object())
                    .setBucketName(response.bucket())
                    .setSize(response.size())
                    .setUserMetadata(response.userMetadata())
                    .setLastModified(response.lastModified().toLocalDateTime());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new MinioHighLevelClientException(e.getLocalizedMessage());
        }
    }


    /**
     * 上传文件到 minio
     *
     * @param bucketName
     * @param objectName 对象名称 支持多级子目录: /a/b/xxx.txt
     * @param file
     * @return 上传后的对象名称(path)
     * @throws MinioHighLevelClientException
     */
    public String putObject(String bucketName, String objectName, File file, PutObjectOptions options) throws MinioHighLevelClientException {
        try {
            if (Objects.isNull(options)) {
                options = new PutObjectOptions();
            }
            MinioObject existed = getObject(bucketName, objectName);
            if (existed != null && existed.getContent().length > 0) {
                if (ObjectNameConflicts.keep_both.equals(options.nameConflicts)) {
                    // 重命名
                    String extName = extName(file);
                    objectName = objectName.replace(".".concat(extName), "").concat(String.valueOf(toTimestamp())) + "." + extName;
                    LOG.info("rename to：{}", objectName);
                } else {
                    LOG.info("object[{}] will be overwritted", objectName);
                }
            }
            ObjectWriteResponse response = client.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(new FileInputStream(file), Files.size(file.toPath()), -1L)
                    .contentType(getContentType(file)).build());
            String object = response.object();
            return object;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new MinioHighLevelClientException(e.getLocalizedMessage());
        }
    }

    public static String getContentType(File file) {
        String contentType = null;
        try {
            contentType = new MimetypesFileTypeMap().getContentType(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentType;
    }

    /**
     * remove object
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws MinioHighLevelClientException
     */
    public boolean removeObject(String bucketName, String objectName) throws MinioHighLevelClientException {
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new MinioHighLevelClientException(e.getLocalizedMessage());
        }
    }

    /**
     * remove object by fullpath
     *
     * @param objectFullPath
     * @return
     * @throws MinioHighLevelClientException
     */
    public boolean removeObject(String objectFullPath) throws MinioHighLevelClientException {
        List<String> list = PATH_SPLITTER.splitToList(objectFullPath);
        return removeObject(list.get(0), list.stream().skip(1L).collect(Collectors.joining("/")));
    }


    private long toTimestamp() {
        Instant instant = LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant();
        return instant.toEpochMilli();
    }


    public static class PutObjectOptions {

        private ObjectNameConflicts nameConflicts = ObjectNameConflicts.keep_both;

        public ObjectNameConflicts getNameConflicts() {
            return nameConflicts;
        }

        public PutObjectOptions setNameConflicts(ObjectNameConflicts nameConflicts) {
            this.nameConflicts = nameConflicts;
            return this;
        }

        public PutObjectOptions() {
        }
    }

    /**
     * 上传文件遇到同名时的处理策略
     */
    public static enum ObjectNameConflicts {
        // 覆盖 替换原有的对象
        overwrite,
        // 并存 后进入的重命名
        keep_both
    }


    /**
     * minio 中的 object
     */
    public static class MinioObject implements Serializable {

        /**
         * 对象元数据
         */
        private ObjectMetadata metadata;

        /**
         * 对象内容
         */
        private byte[] content;

        /**
         * 写入 output
         *
         * @param outputStream
         */
        public void write(OutputStream outputStream) {
            if (this.content.length > 0) {
                try {
                    MinioUtil.write(outputStream, true, this.content);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        /**
         * 写入 file
         *
         * @param file
         */
        public void write(File file) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // write
            try {
                write(new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public ObjectMetadata getMetadata() {
            return metadata;
        }

        public MinioObject setMetadata(ObjectMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public byte[] getContent() {
            return content;
        }

        public MinioObject setContent(byte[] content) {
            this.content = content;
            return this;
        }
    }

    public static class ObjectMetadata implements Serializable {

        private String objectName;

        private String bucketName;

        private String contentType;

        private Map<String, String> userMetadata;

        private Long size;

        private LocalDateTime lastModified;

        public Map<String, String> getUserMetadata() {
            return userMetadata;
        }

        public ObjectMetadata setUserMetadata(Map<String, String> userMetadata) {
            this.userMetadata = userMetadata;
            return this;
        }

        public Long getSize() {
            return size;
        }

        public ObjectMetadata setSize(Long size) {
            this.size = size;
            return this;
        }

        public LocalDateTime getLastModified() {
            return lastModified;
        }

        public ObjectMetadata setLastModified(LocalDateTime lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public String getObjectName() {
            return objectName;
        }

        public ObjectMetadata setObjectName(String objectName) {
            this.objectName = objectName;
            return this;
        }

        public String getBucketName() {
            return bucketName;
        }

        public ObjectMetadata setBucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public String getContentType() {
            return contentType;
        }

        public ObjectMetadata setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }
    }


    public static class Builder {

        private MinioClient minioClient;


        public Builder minioClient(MinioClient client) {
            this.minioClient = client;
            return this;
        }

        public MinioHighLevelClient build() {
            return new MinioHighLevelClient(this);
        }

    }
}
