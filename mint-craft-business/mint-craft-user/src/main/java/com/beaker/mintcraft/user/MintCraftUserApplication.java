package com.beaker.mintcraft.user;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author beaker
 * @Date 2026/4/27 22:48
 * @Description 用户模块启动类
 */
@SpringBootApplication(scanBasePackages = "com.beaker.mintcraft.user")
@EnableDubbo
public class MintCraftUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MintCraftUserApplication.class, args);
    }
}
