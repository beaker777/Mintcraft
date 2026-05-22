package com.beaker.mintcraft.job;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author beaker
 * @Date 2026/5/22 18:17
 * @Description 任务模块参数类
 */
@Getter
@ConfigurationProperties(prefix = XxlJobProperties.PREFIX)
public class XxlJobProperties {

    public static final String PREFIX = "spring.xxl.job";

    private boolean enabled;

    private String adminAddresses;

    private String accessToken;

    private String appName;

    private String ip;

    private int port;

    private String logPath;

    private int logRetentionDays = 30;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }


    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }
}
