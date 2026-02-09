package org.winterfell.misc.oss.impl;

import org.junit.jupiter.api.Test;
import org.winterfell.misc.hutool.mini.MapUtil;
import org.winterfell.misc.hutool.mini.StringUtil;
import org.winterfell.misc.hutool.mini.io.ResourceUtil;
import org.winterfell.misc.oss.OssClient;
import org.winterfell.misc.oss.OssClientException;
import org.winterfell.misc.oss.support.OssPreSignedType;
import org.winterfell.misc.oss.support.option.OssPutObjectOption;
import org.winterfell.misc.oss.support.result.OssBucketResult;
import org.winterfell.misc.oss.support.result.OssGetResult;
import org.winterfell.misc.oss.support.result.OssObjectResult;
import org.winterfell.misc.oss.support.result.OssPutResult;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/11/14
 */
public class S3ObsClientImplTest {

    // rustfs 测试
    OssClient client = new S3ObsClientImpl(new S3ObsClientImpl.Builder()
            .setEndpoint("http://127.0.0.1:9000")
            .setRegion("CHN-NJ")
            .setAccessKey("LZYmID2NcfwgiFdWRb38")
            .setSecretKey("GD7AbrwVdHlCtN0Jy4WuFxhzg3c9Yoea6QpkPXfL"));

    @Test
    void bucketExists() throws OssClientException {
        boolean exists = client.bucketExists("demo");
        assertTrue(exists);
    }

    @Test
    void makeBucket() throws OssClientException {
        boolean b = client.makeBucket("my-test", "CHN-NJ", true, false);
        assertTrue(b);
    }

    @Test
    void setBucketVersioning() {
    }

    @Test
    void removeBucket() {
    }

    @Test
    void listBuckets() throws OssClientException {
        List<OssBucketResult> obsBucketResults = client.listBuckets();
        for (OssBucketResult obsBucketResult : obsBucketResults) {
            System.out.println(StringUtil.format("name: {}, createAt: {}", obsBucketResult.getName(), obsBucketResult.getCreateAt()));
        }
    }

    @Test
    void statObject() {
    }

    @Test
    void putObject() throws OssClientException {
        URL resource = ResourceUtil.getResource("test.json");
        System.out.println(resource);
        File file = new File(resource.getPath());
        if (file.exists()) {
            // put
            OssPutResult putResult = client.putObject("demo", file.getName(), file, new OssPutObjectOption().setMetadata(MapUtil.of("foo", "bar")));
            System.out.println(putResult);
        }
    }

    @Test
    void getObject() throws OssClientException {
        OssGetResult result = client.getObject("demo", "test2.json", null, null);
        System.out.println(result.getLastModified());
    }

    @Test
    void removeObject() {
    }

    @Test
    void listObjects() throws OssClientException {
        List<OssObjectResult> results = client.listObjects("demo", "my", null);
        assertFalse(results.isEmpty());
        for (OssObjectResult result : results) {
            System.out.println(result.getObject());
        }
    }

    @Test
    void getPreSignedObjectUrl() throws OssClientException {
        String url = client.getPreSignedObjectUrl("demo", "myy.json", OssPreSignedType.PUT, 3600);
        System.out.println(url);
    }
}