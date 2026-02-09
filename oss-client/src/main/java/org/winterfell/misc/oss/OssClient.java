package org.winterfell.misc.oss;

import org.winterfell.misc.oss.support.OssBucketVersionStatus;
import org.winterfell.misc.oss.support.OssPreSignedType;
import org.winterfell.misc.oss.support.option.OssListObjectsOption;
import org.winterfell.misc.oss.support.option.OssPutObjectOption;
import org.winterfell.misc.oss.support.result.*;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 定义统一的 oss 接口 屏蔽各家oss的不一致
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/9
 */
public interface OssClient {

    /**
     * bucket exists or not
     *
     * @param bucket
     * @return
     * @throws OssClientException
     */
    boolean bucketExists(String bucket) throws OssClientException;

    /**
     * make bucket
     *
     * @param bucket
     * @param region
     * @param enableVersioned  开启版本控制
     * @param lockObject
     * @return
     * @throws OssClientException
     */
    boolean makeBucket(String bucket, String region, boolean enableVersioned, boolean lockObject) throws OssClientException;

    /**
     * 设置 bucket 的版本控制状态
     * @param bucket
     * @param status
     * @return
     * @throws OssClientException
     */
    boolean setBucketVersioning(String bucket, OssBucketVersionStatus status) throws OssClientException;


    /**
     * remove bucket
     *
     * @param bucket
     * @return
     * @throws OssClientException
     */
    boolean removeBucket(String bucket) throws OssClientException;


    /**
     * 列举桶信息
     * @return
     * @throws OssClientException
     */
    List<OssBucketResult> listBuckets() throws OssClientException;


    /**
     * 获取对象 stats
     *
     * @param bucket    桶名称
     * @param object    多层写法 resumes/xx.pdf
     * @param versionId
     * @return
     * @throws OssClientException
     */
    OssStatResult statObject(String bucket, String object, String versionId) throws OssClientException;


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
    OssPutResult putObject(String bucket, String object, File file, OssPutObjectOption option) throws OssClientException;


    /**
     * 上传流作为object
     * @param bucket
     * @param object
     * @param stream
     * @param option
     * @return
     * @throws OssClientException
     */
    OssPutResult putObject(String bucket, String object, InputStream stream, OssPutObjectOption option) throws OssClientException;


    /**
     * get object
     * @param bucket
     * @param object
     * @return
     * @throws OssClientException
     */
    OssGetResult getObject(String bucket, String object) throws OssClientException;

    /**
     * get object by range
     * @param bucket
     * @param object
     * @param offset  偏移位置
     * @param length  当前获取的长度
     * @return
     * @throws OssClientException
     */
    OssGetResult getObject(String bucket, String object, Long offset, Long length) throws OssClientException;

    /**
     * remove object
     *
     * @param bucket
     * @param object 多层写法： testss/xx.json 需要包含文件扩展名
     * @return
     * @throws OssClientException
     */
    boolean removeObject(String bucket, String object) throws OssClientException;

    /**
     * 删除同一个 bucket下的多个对象
     *
     * @param bucket
     * @param objects
     * @return
     * @throws OssClientException
     */
    boolean removeObjects(String bucket, List<String> objects) throws OssClientException;


    /**
     * 列举桶内的对象
     * @param bucket
     * @param namePrefix  名字前缀 模糊匹配
     * @param option  选项
     * @return
     * @throws OssClientException
     */
    List<OssObjectResult> listObjects(String bucket, String namePrefix, OssListObjectsOption option) throws OssClientException;


    /**
     * 预签名 URL，是一个包含了特定权限和有效期的 URL。通过这个 URL，
     * 用户可以在无需提供认证信息的情况下，直接进行指定的操作，如上传、下载或删除
     * @param bucket
     * @param object
     * @param type
     * @param ttl
     * @return
     * @throws OssClientException
     */
    String getPreSignedObjectUrl(String bucket, String object, OssPreSignedType type, int ttl) throws OssClientException;

}