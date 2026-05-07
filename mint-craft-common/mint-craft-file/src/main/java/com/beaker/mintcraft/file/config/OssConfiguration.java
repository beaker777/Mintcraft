package com.beaker.mintcraft.file.config;

import com.beaker.mintcraft.file.FileService;
import com.beaker.mintcraft.file.OssServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/5/7 20:52
 * @Description OSS 配置类
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssConfiguration {

    @Autowired
    private OssProperties ossProperties;

    @Bean
    public FileService ossService() {
        OssServiceImpl ossService = new OssServiceImpl();

        ossService.setEndPoint(ossProperties.getEndPoint());
        ossService.setBucket(ossProperties.getBucket());
        ossService.setAccessKey(ossProperties.getAccessKey());
        ossService.setAccessSecret(ossProperties.getAccessSecret());

        return ossService;
    }
}
