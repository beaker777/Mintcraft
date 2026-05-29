package com.beaker.mintcraft.base.config;

import com.beaker.mintcraft.base.utils.SpringContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/5/29 21:38
 * @Description base 模块配置类
 */
@Configuration
public class BaseConfiguration {

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
}
