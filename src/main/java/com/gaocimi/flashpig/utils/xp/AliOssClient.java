package com.gaocimi.flashpig.utils.xp;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class AliOssClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AliOssClient.class);

    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.accessId}")
    private String accessKeyId;
    @Value("${oss.accessKey}")
    private String accessKeySecret;
    @Value("${oss.bucket")
    private String bucketName;


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

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 上传某个Object
     *
     * @param bucketUrl 自定义路径/
     * @param inputStream
     * @return
     */
    public boolean putObject(String bucketUrl, InputStream inputStream) {
        OSSClient client = new OSSClient(this.endpoint, this.accessKeyId, this.accessKeySecret);
        try {
            // 上传Object.
            client.putObject(this.bucketName, bucketUrl, inputStream);
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
     * @param bucketUrl
     * @return
     */
    public boolean deleteObject(String bucketUrl) {
        OSSClient client = new OSSClient(this.endpoint, this.accessKeyId, this.accessKeySecret);
        try {
            // 删除Object.
            client.deleteObject(this.bucketName, bucketUrl);
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
