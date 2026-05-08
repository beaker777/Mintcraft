package com.beaker.mintcraft.sms.config;

import com.beaker.mintcraft.sms.SmsService;
import com.beaker.mintcraft.sms.impl.MockSmsServiceImpl;
import com.beaker.mintcraft.sms.impl.SmsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @Author beaker
 * @Date 2026/5/8 18:15
 * @Description 短信发送配置类
 */
@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsConfiguration {

    @Autowired
    private SmsProperties properties;

    @Bean
    @ConditionalOnMissingBean
    @Profile({"default", "prod"})
    public SmsService smsService() {
        SmsServiceImpl smsService = new SmsServiceImpl();
        smsService.setHost(properties.getHost());
        smsService.setPath(properties.getPath());
        smsService.setAppcode(properties.getAppcode());
        smsService.setSmsSignId(properties.getSmsSignId());
        smsService.setTemplateId(properties.getTemplateId());
        return smsService;
    }

    @Bean
    @ConditionalOnMissingBean
    @Profile({"dev", "test"})
    public SmsService mockSmsService() {
        return new MockSmsServiceImpl();
    }
}
