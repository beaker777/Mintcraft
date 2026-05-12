package com.beaker.mintcraft.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author beaker
 * @Date 2026/5/12 21:33
 * @Description 订单模块启动类
 */
@SpringBootApplication(scanBasePackages = "com.beaker.mintcraft.order")
@EnableDubbo
public class MintCraftOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MintCraftOrderApplication.class, args);
    }
}
