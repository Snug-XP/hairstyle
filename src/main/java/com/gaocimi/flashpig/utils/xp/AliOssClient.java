package com.gaocimi.flashpig.utils.xp;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AliOssClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AliOssClient.class);
 
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
 
    public String getAccessKeyId() {
        return accessKeyId;
    }
 
    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }
 
    public String getAccessKeySecret() {
        return accessKeySecret;
    }
 
    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }
 
    public String getEndpoint() {
        return endpoint;
    }
 
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
 
    /**
     * 上传某个Object
     *
     * @param bucketName
     * @param bucketUrl 自定义路径/
     * @param inputStream
     * @return
     */
    public boolean putObject(String bucketName, String bucketUrl, InputStream inputStream) {
        OSSClient client = new OSSClient(this.endpoint, this.accessKeyId, this.accessKeySecret);
        try {
            // 上传Object.
            client.putObject(bucketName, bucketUrl, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            client.shutdown();
        }
        return true;
    }
 
    /**
     * 删除某个Object
     *
     * @param bucketName
     * @param bucketUrl
     * @return
     */
    public boolean deleteObject(String bucketName, String bucketUrl) {
        OSSClient client = new OSSClient(this.endpoint, this.accessKeyId, this.accessKeySecret);
        try {
            // 删除Object.
            client.deleteObject(bucketName, bucketUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            client.shutdown();
        }
        return true;
    }
 
    /**
     * 删除多个Object
     *
     * @param bucketName
     * @param bucketUrls
     * @return
     */
    public boolean deleteObjects(String bucketName, List<String> bucketUrls) {
        OSSClient client = new OSSClient(this.endpoint, this.accessKeyId, this.accessKeySecret);
        try {
            // 删除Object.
            DeleteObjectsResult deleteObjectsResult = client.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(bucketUrls));
            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            client.shutdown();
        }
        return true;
    }



}
