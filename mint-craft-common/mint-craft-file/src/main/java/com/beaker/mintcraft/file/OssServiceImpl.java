package com.beaker.mintcraft.file;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;

/**
 * @Author beaker
 * @Date 2026/5/7 20:32
 * @Description Oss 实现文件上传
 */
@Slf4j
@Setter
public class OssServiceImpl implements FileService {

    private String bucket;

    private String endPoint;

    private String accessKey;

    private String accessSecret;

    @Override
    public boolean upload(String path, InputStream fileStream) {
        // endpoint : 北京
        String endpoint = endPoint;
        // 从环境变量中获取 RAM 用户的访问密钥（AccessKey ID和 AccessKey Secret）。
        String accessKeyId = accessKey;
        String accessKeySecret = accessSecret;
        // 获取访问凭证
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);

        // BucketName : mintcraft
        String bucketName = bucket;
        // Object 完整路径, 不包括 bucketName
        String objectName = path;

        // 创建实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);
        boolean uploadRes = false;
        try {

            // 创建 PutObjectRequest 对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, fileStream);

            // 上传字符串
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            if (StringUtils.isNotBlank(result.getRequestId())) {
                uploadRes = true;
            }
        } catch (Exception e) {
            log.error("OssUtil upload error,path=" + path, e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return uploadRes;
    }

}
