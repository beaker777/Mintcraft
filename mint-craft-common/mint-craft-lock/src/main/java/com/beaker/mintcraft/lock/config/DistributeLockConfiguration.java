package com.beaker.mintcraft.lock.config;

import com.beaker.mintcraft.lock.DistributeLockAspect;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/5/7 14:27
 * @Description 分布式锁配置类
 */
@Configuration
public class DistributeLockConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DistributeLockAspect distributeLockAspect(RedissonClient redisson) {
        return new DistributeLockAspect(redisson);
    }
}
