package org.winterfell.misc.oss.impl;

import org.winterfell.misc.hutool.mini.StringUtil;
import org.winterfell.misc.oss.OssClientException;
import org.winterfell.misc.oss.support.OssBucketVersionStatus;
import org.winterfell.misc.oss.support.OssPreSignedType;
import org.winterfell.misc.oss.support.option.OssListObjectsOption;
import org.winterfell.misc.oss.support.option.OssPutObjectOption;
import org.winterfell.misc.oss.support.result.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.DeleteObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * TESTME:
 * amazon s3 client
 * - rustfs
 * </p>
 *
 * @author Alex
 * @since 2025/11/5
 */
public class S3ObsClientImpl extends AbstractOssClient {

    private final S3Client s3;

    private final S3Presigner presigner;

    public S3ObsClientImpl(Builder builder) {
        super(builder);
        this.s3 = S3Client.builder()
                // RustFS 地址
                .endpointOverride(URI.create(builder.endpoint))
                // 可写死，RustFS 不校验 region
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(builder.accessKey, builder.secretKey)))
                // 关键配置！RustFS 需启用 Path-Style
                .forcePathStyle(true)
                .build();
        this.presigner = S3Presigner.builder()
                .endpointOverride(URI.create(builder.endpoint))
                .region(Region.of(builder.region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(builder.accessKey, builder.secretKey)))
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
            s3.headBucket(b -> b.bucket(bucket));
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        } catch (Exception e) {
            throw new OssClientException("Failed to check if bucket exists.", e);
        }
    }

    /**
     * make bucket
     *
     * @param bucket
     * @param region
     * @param enableVersioned 开启版本控制
     * @param lockObject
     * @return
     * @throws OssClientException
     */
    @Override
    public boolean makeBucket(String bucket, String region, boolean enableVersioned, boolean lockObject) throws OssClientException {
        try {
            s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            return true;
        } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException e) {
            throw new OssClientException("Bucket already exists.");
        }
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
            // 将 OssBucketVersionStatus 转换为 S3 的版本控制状态
            String statusStr = status == OssBucketVersionStatus.Enabled ? "Enabled" : "Suspended";
            // 构建版本控制配置请求
            PutBucketVersioningRequest request = PutBucketVersioningRequest.builder()
                    .bucket(bucket)
                    .versioningConfiguration(vc -> vc.status(statusStr))
                    .build();
            // 执行设置版本控制
            PutBucketVersioningResponse response = s3.putBucketVersioning(request);
            return response.sdkHttpResponse().isSuccessful();
        } catch (Exception e) {
            throw new OssClientException("Failed to set bucket versioning status.", e);
        }
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
        try {
            // 构建删除存储桶请求
            DeleteBucketRequest request = DeleteBucketRequest.builder()
                    .bucket(bucket)
                    .build();
            // 执行删除操作
            DeleteBucketResponse response = s3.deleteBucket(request);
            return response.sdkHttpResponse().isSuccessful();
        } catch (Exception e) {
            throw new OssClientException("Failed to remove bucket: " + bucket, e);
        }
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
            // 获取存储桶列表
            ListBucketsResponse response = s3.listBuckets();
            // 转换为 OssBucketResult 列表
            return response.buckets().stream()
                    .map(s3Bucket -> new OssBucketResult(
                            s3Bucket.name(),
                            LocalDateTime.ofInstant(s3Bucket.creationDate(), ZoneId.systemDefault())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new OssClientException("Failed to list buckets.", e);
        }
    }

    /**
     * 获取对象 stats
     *
     * @param bucket    桶名称
     * @param object    多层写法 resumes/xx.pdf
     * @param versionId
     * @return
     * @throws OssClientException
     */
    @Override
    public OssStatResult statObject(String bucket, String object, String versionId) throws OssClientException {
        try {
            // 构建 HeadObject 请求
            HeadObjectRequest.Builder requestBuilder = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(object);
            // 如果提供了版本 ID，则添加到请求中
            if (versionId != null && !versionId.isEmpty()) {
                requestBuilder.versionId(versionId);
            }
            // 执行 HeadObject 操作
            HeadObjectResponse response = s3.headObject(requestBuilder.build());

            OssStatResult result = new OssStatResult(
                    response.contentType(),
                    response.contentLength(),
                    LocalDateTime.ofInstant(response.lastModified(), ZoneId.systemDefault()));
            result.setObject(object).setBucket(bucket).setVersionId(response.versionId()).setUserMetadata(response.metadata());
            return result;
        } catch (NoSuchKeyException e) {
            throw new OssClientException("Object not found: " + object, e);
        } catch (Exception e) {
            throw new OssClientException("Failed to stat object: " + object, e);
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
            // 构建 PutObject 请求
            PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(object);
            // 如果提供了选项，则设置相关属性
            if (option != null) {
                if (StringUtil.isNotBlank(option.getContentType())) {
                    requestBuilder.contentType(option.getContentType());
                }
                if (StringUtil.isNotBlank(option.getContentEncoding())) {
                    requestBuilder.contentEncoding(option.getContentEncoding());
                }
                // 设置用户自定义元数据
                if (option.getMetadata() != null && !option.getMetadata().isEmpty()) {
                    requestBuilder.metadata(option.getMetadata());
                }
            }
            // 执行上传文件操作
            PutObjectResponse response = s3.putObject(requestBuilder.build(), file.toPath());
            OssPutResult putResult = new OssPutResult();
            putResult.setBucket(bucket).setObject(object).setVersionId(response.versionId());
            if (option != null) {
                putResult.setUserMetadata(option.getMetadata());
                putResult.setContentType(option.getContentType());
            }
            return putResult;
        } catch (Exception e) {
            throw new OssClientException("Failed to put object: " + object, e);
        }
    }

    /**
     * 上传流作为object
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
            // 构建 PutObject 请求
            PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(object);
            if (option != null) {
                if (StringUtil.isNotBlank(option.getContentType())) {
                    requestBuilder.contentType(option.getContentType());
                }
                if (StringUtil.isNotBlank(option.getContentEncoding())) {
                    requestBuilder.contentEncoding(option.getContentEncoding());
                }
                // 设置用户自定义元数据
                if (option.getMetadata() != null && !option.getMetadata().isEmpty()) {
                    requestBuilder.metadata(option.getMetadata());
                }
            }
            // 执行上传流操作
            PutObjectResponse response = s3.putObject(requestBuilder.build(), RequestBody.fromInputStream(stream, stream.available()));
            OssPutResult putResult = new OssPutResult();
            putResult.setBucket(bucket).setObject(object).setVersionId(response.versionId());
            if (option != null) {
                putResult.setUserMetadata(option.getMetadata());
                putResult.setContentType(option.getContentType());
            }
            return putResult;
        } catch (Exception e) {
            throw new OssClientException("Failed to put object: " + object, e);
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
     * get object by range
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
        try {
            // 构建 GetObject 请求
            GetObjectRequest.Builder requestBuilder = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(object);

            // 如果提供了范围参数，则设置 Range 头
            if (offset != null || length != null) {
                StringBuilder rangeBuilder = new StringBuilder("bytes=");
                if (offset != null) {
                    rangeBuilder.append(offset);
                }
                rangeBuilder.append("-");
                if (length != null && offset != null) {
                    rangeBuilder.append(offset + length - 1);
                } else if (length != null) {
                    rangeBuilder.append(length - 1);
                }
                requestBuilder.range(rangeBuilder.toString());
            }

            // 执行 GetObject 操作
            ResponseInputStream<GetObjectResponse> response = s3.getObject(requestBuilder.build());
            OssGetResult result = new OssGetResult();
            result.setBucket(bucket)
                    .setObject(object)
                    .setVersionId(response.response().versionId())
                    .setUserMetadata(response.response().metadata());
            result.setLastModified(LocalDateTime.ofInstant(response.response().lastModified(), ZoneId.systemDefault()))
                    .setContentLength(response.response().contentLength())
                    .setContentType(response.response().contentType())
                    .setContent(response);
            return result;
        } catch (NoSuchKeyException e) {
            throw new OssClientException("Object not found: " + object, e);
        } catch (Exception e) {
            throw new OssClientException("Failed to get object: " + object, e);
        }
    }

    /**
     * remove object
     *
     * @param bucket
     * @param object 多层写法： testss/xx.json 需要包含文件扩展名
     * @return
     * @throws OssClientException
     */
    @Override
    public boolean removeObject(String bucket, String object) throws OssClientException {
        try {
            // 构建 DeleteObject 请求
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(object)
                    .build();
            DeleteObjectResponse response = s3.deleteObject(request);
            // 返回操作是否成功
            return response.sdkHttpResponse().isSuccessful();
        } catch (Exception e) {
            throw new OssClientException("Failed to remove object: " + object, e);
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
        try {
            // 构建 DeleteObjects 请求
            DeleteObjectsRequest.Builder requestBuilder = DeleteObjectsRequest.builder()
                    .bucket(bucket);

            // 将对象列表转换为 ObjectIdentifier 列表
            List<ObjectIdentifier> objectIdentifiers = objects.stream()
                    .map(object -> ObjectIdentifier.builder().key(object).build())
                    .collect(Collectors.toList());
            // 设置要删除的对象列表
            requestBuilder.delete(Delete.builder().objects(objectIdentifiers).build());
            // 执行批量删除操作
            DeleteObjectsResponse response = s3.deleteObjects(requestBuilder.build());
            // 返回操作是否成功（检查是否有错误结果）
            return response.errors().isEmpty();
        } catch (Exception e) {
            throw new OssClientException("Failed to remove objects.", e);
        }
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
        try {
            // 构建 ListObjectsV2 请求
            ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                    .bucket(bucket);
            // 如果提供了前缀，则设置前缀过滤条件
            if (StringUtil.isNotBlank(namePrefix)) {
                requestBuilder.prefix(namePrefix);
            }
            if (option != null) {
                // 设置最大返回对象数量
                if (option.getMaxKeys() > 0) {
                    requestBuilder.maxKeys(option.getMaxKeys());
                }
            }
            ListObjectsV2Response response = s3.listObjectsV2(requestBuilder.build());
            // 转换为 OssObjectResult 列表
            return response.contents().stream()
                    .map(s3Object -> {
                        OssObjectResult result = new OssObjectResult();
                        result.setBucket(bucket)
                                .setObject(s3Object.key());
                        result.setSize(s3Object.size())
                                .setLastModified(LocalDateTime.ofInstant(s3Object.lastModified(), ZoneId.systemDefault()))
                                .setEtag(s3Object.eTag().replace("\"", "")); // 移除 ETag 前后的引号
                        return result;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new OssClientException("Failed to list objects in bucket: " + bucket, e);
        }
    }

    /**
     * 预签名 URL，是一个包含了特定权限和有效期的 URL。通过这个 URL，
     * 用户可以在无需提供认证信息的情况下，直接进行指定的操作，如上传、下载或删除
     *
     * @param bucket
     * @param object
     * @param type
     * @param ttl
     * @return
     * @throws OssClientException
     */
    @Override
    public String getPreSignedObjectUrl(String bucket, String object, OssPreSignedType type, int ttl) throws OssClientException {
        try {
            // 根据类型构建不同的预签名请求
            switch (type) {
                case GET:
                    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(object)
                            .build();
                    return presigner.presignGetObject(GetObjectPresignRequest.builder()
                            .getObjectRequest(getObjectRequest)
                            .signatureDuration(Duration.ofSeconds(ttl))
                            .build()).url().toString();

                case PUT:
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(object)
                            .build();
                    return presigner.presignPutObject(PutObjectPresignRequest.builder()
                            .putObjectRequest(putObjectRequest)
                            .signatureDuration(Duration.ofSeconds(ttl))
                            .build()).url().toString();

                case DELETE:
                    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(object)
                            .build();
                    return presigner.presignDeleteObject(DeleteObjectPresignRequest.builder()
                            .deleteObjectRequest(deleteObjectRequest)
                            .signatureDuration(Duration.ofSeconds(ttl))
                            .build()).url().toString();
                default:
                    throw new OssClientException("Unsupported pre-signed type: " + type);
            }
        } catch (Exception e) {
            throw new OssClientException("Failed to generate pre-signed URL for object: " + object, e);
        }
    }

    public static class Builder extends AbstractOssClient.Builder<S3ObsClientImpl, Builder> {

        /**
         * 生成实现
         *
         * @return
         */
        @Override
        public S3ObsClientImpl build() {
            return new S3ObsClientImpl(this);
        }
    }
}