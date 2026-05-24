package com.beaker.mintcraft.pay;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author beaker
 * @Date 2026/5/24 14:47
 * @Description 支付模块启动类
 */
@SpringBootApplication(scanBasePackages = "com.beaker.mintcraft.pay")
@EnableDubbo
public class MintCraftPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MintCraftPayApplication.class, args);
    }
}
