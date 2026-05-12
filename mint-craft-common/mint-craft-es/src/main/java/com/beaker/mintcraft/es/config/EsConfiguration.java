package com.beaker.mintcraft.es.config;

import org.dromara.easyes.starter.register.EsMapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/5/12 18:42
 * @Description ES 配置类
 */
@Configuration
@EsMapperScan("com.beaker.mintcraft.*.infrastructure.es.mapper")
@ConditionalOnProperty(value = "easy-es.enable", havingValue = "true")
public class EsConfiguration {
}
