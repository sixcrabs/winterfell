package org.winterfell.misc.oss.support;

/**
 * <p>
 * obs 对象存储供应者类型
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/6
 */
public enum OssProviderType {

    // MinIO
    minio,
    // 阿里云对象存储
    oss,
    // 华为云对象存储
    obs,
    // 腾讯云对象存储
    cos,
    // 亚马逊云存储
    amazon_s3,
    // 开源Ceph
    ceph
}