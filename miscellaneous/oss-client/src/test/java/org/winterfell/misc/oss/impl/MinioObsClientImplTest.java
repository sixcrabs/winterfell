package org.winterfell.misc.oss.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.winterfell.misc.hutool.mini.io.ResourceUtil;
import org.winterfell.misc.oss.OssClient;
import org.winterfell.misc.oss.OssClientException;
import org.winterfell.misc.oss.support.OssPreSignedType;
import org.winterfell.misc.oss.support.option.OssListObjectsOption;
import org.winterfell.misc.oss.support.option.OssPutObjectOption;
import org.winterfell.misc.oss.support.result.OssBucketResult;
import org.winterfell.misc.oss.support.result.OssObjectResult;
import org.winterfell.misc.oss.support.result.OssPutResult;
import org.winterfell.misc.oss.support.result.OssStatResult;

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
public class MinioObsClientImplTest {

    static OssClient obsClient;

    @BeforeAll
    static void pre() {
        obsClient = new MinioObsClientImpl.Builder()
                .setEndpoint("http://127.0.0.1:9000")
                .setAccessKey("XztbHSyw4Hlkr0S45F15")
                .setSecretKey("fIO4N2HHN7o4z2kv9sjOY8ccAE5MoxpLsoRl7cuJ").build();
    }

    @Test
    void bucketExists() throws OssClientException {
        boolean vmapExisted = obsClient.bucketExists("vmap");
        System.out.println("vmap existed: " + vmapExisted);
        boolean exists = obsClient.bucketExists("my-bucket");
        System.out.println("my-bucket existed: " + exists);
    }

    @Test
    void makeBucket() throws OssClientException {
        boolean bucket = obsClient.makeBucket("vtile", "", false, true);
        System.out.println(bucket);
    }

    @Test
    void setBucketVersioning() {
    }

    @Test
    void removeBucket() throws OssClientException {
        boolean b = obsClient.removeBucket("vtile");
        System.out.println(b);
    }

    @Test
    void listBuckets() throws OssClientException {
        List<OssBucketResult> bucketResults = obsClient.listBuckets();
        for (OssBucketResult bucketResult : bucketResults) {
            System.out.println(bucketResult);
        }
    }

    @Test
    void statObject() throws OssClientException {
        OssStatResult statResult = obsClient.statObject("vmap", "pingdu_regions.geojson", null);
        System.out.println(statResult);
    }

    @Test
    void putObject() throws OssClientException {
        URL resource = ResourceUtil.getResource("test.json");
        System.out.println(resource);
        File file = new File(resource.getPath());
        if (file.exists()) {
            // put
            OssPutResult putResult = obsClient.putObject("vmap", "alex11/".concat(file.getName()), file, new OssPutObjectOption());
            System.out.println(putResult);
        }
    }

    @Test
    void testPutObject() {
    }

    @Test
    void getObject() {
    }

    @Test
    void removeObject() throws OssClientException {
        boolean b = obsClient.removeObject("vmap", "data123/test.xlsx");
        System.out.println(b);
    }

    @Test
    void listObjects() throws OssClientException {
        List<OssObjectResult> results = obsClient.listObjects("vmap", "", new OssListObjectsOption());
        for (OssObjectResult result : results) {
            System.out.println(result);
        }
    }

    @Test
    void getPreSignedObjectUrl() throws OssClientException {
        String url = obsClient.getPreSignedObjectUrl("vmap", "tif/0813.tiff", OssPreSignedType.GET, 300);
        System.out.println(url);
    }
}