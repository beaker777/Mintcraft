package com.beaker.mintcraft.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author beaker
 * @Date 2026/5/7 20:37
 * @Description Oss 参数类
 */
@ConfigurationProperties(prefix = OssProperties.PREFIX)
public class OssProperties {

    public static final String PREFIX = "spring.oss";

    private String bucket;

    private String endPoint;

    private String accessKey;

    private String accessSecret;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }
}
