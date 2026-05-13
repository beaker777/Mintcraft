package com.beaker.mintcraft.order.config;

import com.beaker.mintcraft.order.sharding.id.WorkerIdHolder;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/5/13 15:31
 * @Description 富客户端配置类
 */
@Configuration
public class OrderClientConfiguration {

    @Bean
    public WorkerIdHolder workerIdHolder(RedissonClient redisson) {
        return new WorkerIdHolder(redisson);
    }
}
