package com.beaker.mintcraft.user.domain.service.config;

import com.beaker.mintcraft.user.domain.service.AuthService;
import com.beaker.mintcraft.user.domain.service.impl.AuthServiceImpl;
import com.beaker.mintcraft.user.domain.service.impl.MockAuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @Author beaker
 * @Date 2026/5/9 16:31
 * @Description 实名认证配置类
 */
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class AuthConfiguration {

    @Autowired
    private AuthProperties authProperties;

    @Bean
    @ConditionalOnMissingBean
    @Profile({"default", "prod", "dev"})
    public AuthService authService() {
        return new AuthServiceImpl(authProperties.getHost(), authProperties.getPath(), authProperties.getAppcode());
    }

    @Bean
    @ConditionalOnMissingBean
    @Profile({"test"})
    public AuthService mockAuthService() {
        return new MockAuthServiceImpl();
    }
}
