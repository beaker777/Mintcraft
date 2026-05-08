package com.beaker.mintcraft.notice;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author beaker
 * @Date 2026/5/8 18:48
 * @Description notice 模块启动类
 */
@SpringBootApplication(scanBasePackages = "com.beaker.mintcraft.notice")
@EnableDubbo
public class MintCraftNoticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MintCraftNoticeApplication.class, args);
    }
}
