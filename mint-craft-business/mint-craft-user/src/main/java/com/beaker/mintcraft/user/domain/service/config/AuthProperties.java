package com.beaker.mintcraft.user.domain.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author beaker
 * @Date 2026/5/9 16:32
 * @Description 实名认证参数类
 */
@Data
@ConfigurationProperties(prefix = AuthProperties.PREFIX)
public class AuthProperties {

    public static final String PREFIX = "spring.auth";

    private String host;

    private String path;

    private String appcode;
}
