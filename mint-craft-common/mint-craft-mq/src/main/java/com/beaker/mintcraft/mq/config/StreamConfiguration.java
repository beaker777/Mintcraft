package com.beaker.mintcraft.mq.config;

import com.beaker.mintcraft.mq.producer.StreamProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/5/16 19:57
 * @Description MQ 配置类
 */
@Configuration
public class StreamConfiguration {

    @Bean
    public StreamProducer streamProducer() {
        return new StreamProducer();
    }
}
