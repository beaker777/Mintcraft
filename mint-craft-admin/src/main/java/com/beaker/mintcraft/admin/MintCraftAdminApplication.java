package com.beaker.mintcraft.admin;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author beaker
 * @Date 2026/6/1 17:51
 * @Description admin 模块启动类
 */
@SpringBootApplication(scanBasePackages = "com.beaker.mintcraft.admin")
@EnableDubbo
public class MintCraftAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MintCraftAdminApplication.class, args);
    }
}
