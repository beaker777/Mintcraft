package com.beaker.mintcraft.order.domain.listener.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author beaker
 * @Date 2026/5/15 21:16
 * @Description 订单监听器配置类
 */
@Configuration
@EnableAsync
public class OrderListenerConfiguration {

    @Bean("orderListenExecutor")
    public Executor orderListenExecutor(MeterRegistry registry) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                16,
                32,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );

        ExecutorServiceMetrics.monitor(registry, executor, "orderListenExecutor");
        return executor;
    }
}
