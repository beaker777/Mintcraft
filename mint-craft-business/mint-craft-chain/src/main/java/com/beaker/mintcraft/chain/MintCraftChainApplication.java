package com.beaker.mintcraft.chain;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author beaker
 * @Date 2026/5/24 19:31
 * @Description 链模块启动类
 */
@SpringBootApplication(scanBasePackages = "com.beaker.mintcraft.chain")
@EnableDubbo
public class MintCraftChainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MintCraftChainApplication.class, args);
    }
}
