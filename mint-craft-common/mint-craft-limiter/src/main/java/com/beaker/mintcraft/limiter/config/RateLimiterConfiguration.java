package com.beaker.mintcraft.limiter.config;

import com.beaker.mintcraft.limiter.SlidingWindowRateLimiter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/5/8 20:22
 * @Description 限流器配置类
 */
@Configuration
public class RateLimiterConfiguration {

    @Bean
    public SlidingWindowRateLimiter slidingWindowRateLimiter(RedissonClient redissonClient) {
        return new SlidingWindowRateLimiter(redissonClient);
    }
}
