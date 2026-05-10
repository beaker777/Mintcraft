package com.beaker.mintcraft.inventory;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author beaker
 * @Date 2026/5/10 21:50
 * @Description 库存模块启动类
 */
@SpringBootApplication(scanBasePackages = "com.beaker.mintcraft.inventory")
@EnableDubbo
public class MintCraftInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MintCraftInventoryApplication.class, args);
    }
}
