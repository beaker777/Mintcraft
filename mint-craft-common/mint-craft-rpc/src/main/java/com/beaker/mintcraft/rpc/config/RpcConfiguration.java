package com.beaker.mintcraft.rpc.config;

import com.beaker.mintcraft.rpc.facade.FacadeAspect;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/4/27 18:29
 * @Description Rpc 配置
 */
@EnableDubbo
@Configuration
public class RpcConfiguration {

    @Bean
    public FacadeAspect facadeAspect() {
        return new FacadeAspect();
    }
}
