package org.winterfell.misc.oss.support;

/**
 * <p>
 * 预签名类型 目前不提供删除操作
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/12
 */
public enum OssPreSignedType {

    // 下载 获取
    GET,
    // 上传  更新
    PUT,
    // post
    POST,
    // 删除
    DELETE
}