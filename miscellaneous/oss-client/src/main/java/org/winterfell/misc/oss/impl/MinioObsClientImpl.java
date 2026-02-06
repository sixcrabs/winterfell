package org.winterfell.misc.oss.impl;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterfell.misc.hutool.mini.StringUtil;
import org.winterfell.misc.oss.OssClientException;
import org.winterfell.misc.oss.support.OssBucketVersionStatus;
import org.winterfell.misc.oss.support.OssHelper;
import org.winterfell.misc.oss.support.OssObjectNameConflicts;
import org.winterfell.misc.oss.support.OssPreSignedType;
import org.winterfell.misc.oss.support.option.OssListObjectsOption;
import org.winterfell.misc.oss.support.option.OssPutObjectOption;
import org.winterfell.misc.oss.support.result.*;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.winterfell.misc.oss.support.OssHelper.getContentType;
import static org.winterfell.misc.oss.support.OssHelper.toTimeStamp;


/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/9
 */
public class MinioObsClientImpl extends AbstractOssClient {

    private static final Logger logger = LoggerFactory.getLogger(MinioObsClientImpl.class);

    private final MinioClient client;

    protected MinioObsClientImpl(Builder builder) {
        super(builder);
        // 初始化 client
        client = MinioClient.builder()
                .endpoint(builder.endpoint)
                .credentials(builder.accessKey, builder.secretKey)
                .region(builder.region)
                .build();
    }

    /**
     * bucket exists or not
     *
     * @param bucket
     * @return
     * @throws OssClientException
     */
    @Override
    public boolean bucketExists(String bucket) throws OssClientException {
        try {
            return client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new OssClientException(e.getLocalizedMessage());
        }
    }

    /**
     * make bucket
     *
     * @param bucket
     * @return
     * @throws OssClientException
     */
    @Override
    public boolean makeBucket(String bucket, String region, boolean enableVersioned, boolean lockObject) throws OssClientException {
        if (!bucketExists(bucket)) {
            MakeBucketArgs.Builder builder = MakeBucketArgs.builder().bucket(bucket);
            if (StringUtil.isNotBlank(region)) {
                builder.region(region);
            }
            builder.objectLock(lockObject);
            try {
                client.makeBucket(builder.build());
                if (enableVersioned) {
                    setBucketVersioning(bucket, OssBucketVersionStatus.Enabled);
                }
                return true;
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                throw new OssClientException(e);
            }
        } else {
            logger.warn("bucket [{}] existed already", bucket);
        }
        return false;
    }


    /**
     * 设置 bucket 的版本控制状态
     *
     * @param bucket
     * @param status
     * @return
     * @throws OssClientException
     */
    @Override
    public boolean setBucketVersioning(String bucket, OssBucketVersionStatus status) throws OssClientException {
        try {
            if (bucketExists(bucket)) {
                client.setBucketVersioning(
                        SetBucketVersioningArgs.builder()
                                .bucket(bucket)
                                .config(new VersioningConfiguration(VersioningConfiguration.Status.fromString(status.name()), false)).build());
                return true;
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new OssClientException(e);
        }
        return false;
    }

    /**
     * remove bucket
     *
     * @param bucket
     * @return
     * @throws OssClientException
     */
    @Override
    public boolean removeBucket(String bucket) throws OssClientException {
        boolean found = bucketExists(bucket);
        if (found) {
            try {
                client.removeBucket(RemoveBucketArgs.builder().bucket(bucket).build());
                return true;
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                throw new RuntimeException(e);
            }
        } else {
            logger.warn("bucket [{}] does not exist", bucket);
        }
        return false;
    }

    /**
     * 列举桶信息
     *
     * @return
     * @throws OssClientException
     */
    @Override
    public List<OssBucketResult> listBuckets() throws OssClientException {
        try {
            List<Bucket> bucketList = client.listBuckets();
            return bucketList.stream().map(tmp ->
                            new OssBucketResult().setName(tmp.name()).setCreateAt(tmp.creationDate().toLocalDateTime()))
                    .collect(Collectors.toList());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new OssClientException(e);
        }
    }

    /**
     * 获取对象 stats
     *
     * @param bucket
     * @param object
     * @param versionId 版本号 可不传
     * @return
     * @throws OssClientException
     */
    @Override
    public OssStatResult statObject(@NotEmpty String bucket, @NotEmpty String object, String versionId) throws OssClientException {
        StatObjectArgs.Builder builder = StatObjectArgs.builder()
                .bucket(bucket)
                .object(object);
        if (StringUtil.isNotBlank(versionId)) {
            builder.versionId(versionId);
        }
        try {
            StatObjectResponse statObject = client.statObject(builder.build());
            OssStatResult result = new OssStatResult();
            result.setObject(object)
                    .setBucket(bucket)
                    .setVersionId(statObject.versionId())
                    .setUserMetadata(statObject.userMetadata());
            result.setSize(statObject.size())
                    .setContentType(statObject.contentType())
                    .setLastModified(Objects.isNull(statObject.lastModified()) ? null : (statObject.lastModified().toLocalDateTime()));
            return result;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new OssClientException(e);
        }
    }

    /**
     * put file as object
     *
     * @param bucket
     * @param object 多层写法： testss/xx.json
     * @param file
     * @param option
     * @return
     * @throws OssClientException
     */
    @Override
    public OssPutResult putObject(String bucket, String object, File file, OssPutObjectOption option) throws OssClientException {
        try {
            if (Objects.isNull(option)) {
                option = new OssPutObjectOption();
            }
            Tags objectTags = null;
            try {
                objectTags = client.getObjectTags(GetObjectTagsArgs.builder().bucket(bucket).object(object).build());
            } catch (ErrorResponseException e) {
                logger.warn(e.errorResponse().toString());
            }
            if (objectTags != null) {
                // 处理同名对象
                if (OssObjectNameConflicts.keep_both.equals(option.getNameConflicts())) {
                    String extName = OssHelper.extName(file);
                    object = object.replace(".".concat(extName), "").concat(String.valueOf(toTimeStamp())) + "." + extName;
                    logger.info("rename to：{}", object);
                } else {
                    logger.info("object [{}] will be renamed", object);
                }
            }
            Path path = file.toPath();
            String contentType = getContentType(file);
            return putObject(bucket, object, Files.newInputStream(path), option.setContentType(contentType));
        } catch (Exception e) {
            throw new OssClientException(e.getLocalizedMessage());
        }
    }

    /**
     * 上传流作为object 会覆盖同名对象
     *
     * @param bucket
     * @param object
     * @param stream
     * @param option
     * @return
     * @throws OssClientException
     */
    @Override
    public OssPutResult putObject(String bucket, String object, InputStream stream, OssPutObjectOption option) throws OssClientException {
        try {
            PutObjectArgs.Builder builder = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(object)
                    .userMetadata(option.getMetadata())
                    .stream(stream, stream.available(), -1L);
            if (StringUtil.isNotBlank(option.getContentType())) {
                builder.contentType(option.getContentType());
            }
            ObjectWriteResponse response = client.putObject(builder.contentType(option.getContentType()).build());

            OssPutResult result = new OssPutResult();
            result.setObject(response.object())
                    .setBucket(response.bucket())
                    .setUserMetadata(option.getMetadata())
                    .setVersionId(response.versionId());
            result.setContentType(option.getContentType());
            return result;
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new OssClientException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.error(e.getLocalizedMessage());
                }
            }
        }
    }

    /**
     * get object
     *
     * @param bucket
     * @param object
     * @return
     * @throws OssClientException
     */
    @Override
    public OssGetResult getObject(String bucket, String object) throws OssClientException {
        return getObject(bucket, object, null, null);
    }

    /**
     * get object 支持分段获取
     *
     * @param bucket
     * @param object
     * @param offset 偏移位置
     * @param length 当前获取的长度
     * @return
     * @throws OssClientException
     */
    @Override
    public OssGetResult getObject(String bucket, String object, Long offset, Long length) throws OssClientException {
        GetObjectArgs.Builder builder = GetObjectArgs.builder()
                .bucket(bucket)
                .object(object);
        if (offset != null && offset >= 0 && length != null && length > 0) {
            builder.length(length).offset(offset);
        }
        try (GetObjectResponse response = client.getObject(builder.build())) {
//            byte[] buf = new byte[1024];
//            int len;
//            byte[] bytes;
//            try (FastByteArrayOutputStream byteArrayOutputStream = new FastByteArrayOutputStream()) {
//                while ((len = response.read(buf)) != -1) {
//                    byteArrayOutputStream.write(buf, 0, len);
//                }
//                byteArrayOutputStream.flush();
//                bytes = byteArrayOutputStream.toByteArray();
//            }
            OssGetResult result = new OssGetResult().setContent(response);
            result.setBucket(result.getBucket())
                    .setObject(result.getObject());
            return result;

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new OssClientException(e.getLocalizedMessage());
        }
    }

    /**
     * remove object
     *
     * @param bucket
     * @param object
     * @return
     * @throws OssClientException
     */
    @Override
    public boolean removeObject(String bucket, String object) throws OssClientException {
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(object).build());
            return true;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new OssClientException(e.getLocalizedMessage());
        }
    }

    /**
     * 删除同一个 bucket下的多个对象
     *
     * @param bucket
     * @param objects
     * @return
     * @throws OssClientException
     */
    @Override
    public boolean removeObjects(String bucket, List<String> objects) throws OssClientException {
        List<DeleteObject> list = objects.stream().map(DeleteObject::new).collect(Collectors.toList());
        boolean ret = true;
        try {
            Iterable<Result<DeleteError>> results =
                    client.removeObjects(RemoveObjectsArgs.builder().bucket(bucket).objects(list).build());
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                logger.error("Error in deleting object: {} ; {} ", error.objectName(), error.message());
                ret = false;
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }


    /**
     * 列举桶内的对象
     *
     * @param bucket
     * @param namePrefix 名字前缀 模糊匹配
     * @param option     选项
     * @return
     * @throws OssClientException
     */
    @Override
    public List<OssObjectResult> listObjects(String bucket, String namePrefix, OssListObjectsOption option) throws OssClientException {
        ListObjectsArgs.Builder builder = ListObjectsArgs.builder().bucket(bucket);
        builder.includeUserMetadata(option.isIncludeUserMetadata())
                .includeVersions(option.isIncludeVersions())
                .maxKeys(option.getMaxKeys())
                .recursive(option.isRecursively());
        if (StringUtil.isNotBlank(namePrefix)) {
            builder.prefix(namePrefix);
        }
        List<OssObjectResult> ret = new ArrayList<>(1);
        try {
            Iterable<Result<Item>> results = client.listObjects(builder.build());
            for (Result<Item> itemResult : results) {
                Item item = itemResult.get();
                OssObjectResult objectResult = new OssObjectResult();
                objectResult.setObject(item.objectName())
                        .setBucket(bucket)
                        .setVersionId(item.versionId())
                        .setUserMetadata(item.userMetadata());
                objectResult.setDir(item.isDir());
                if (!item.isDir()) {
                    objectResult.setSize(item.size())
                            .setLastModified(Objects.isNull(item.lastModified()) ? null : (item.lastModified().toLocalDateTime()));
                }
                ret.add(objectResult);
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    /**
     * 预签名 URL，是一个包含了特定权限和有效期的 URL。通过这个 URL，
     * 用户可以在无需提供认证信息的情况下，直接进行指定的操作，如上传、下载或删除
     *
     * @param bucket
     * @param object
     * @param type
     * @param ttl    单位 秒
     * @return
     * @throws OssClientException
     */
    @Override
    public String getPreSignedObjectUrl(String bucket, String object, OssPreSignedType type, int ttl) throws OssClientException {
        try {
            return client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.valueOf(type.name()))
                            .bucket(bucket)
                            .object(object)
                            .expiry(ttl)
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Builder extends AbstractOssClient.Builder<MinioObsClientImpl, Builder> {

        /**
         * 生成实现
         *
         * @return
         */
        @Override
        public MinioObsClientImpl build() {
            return new MinioObsClientImpl(this);
        }
    }


}