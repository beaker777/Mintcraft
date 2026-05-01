package com.beaker.mintcraft.auth;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author beaker
 * @Date 2026/5/1 00:31
 * @Description auth 模块启动类
 */
@SpringBootApplication(scanBasePackages = {"com.beaker.mintcraft.auth"})
@EnableDubbo
public class MintCraftAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MintCraftAuthApplication.class, args);
    }
}
