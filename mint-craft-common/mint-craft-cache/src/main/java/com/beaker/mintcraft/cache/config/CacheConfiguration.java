package com.beaker.mintcraft.cache.config;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/4/29 15:41
 * @Description 缓存配置类
 */
@Configuration
@EnableMethodCache(basePackages = "com.beaker.mintcraft")
public class CacheConfiguration {
}
