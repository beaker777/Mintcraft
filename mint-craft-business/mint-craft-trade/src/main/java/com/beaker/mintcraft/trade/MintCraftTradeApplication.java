package com.beaker.mintcraft.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author beaker
 * @Date 2026/5/18 14:08
 * @Description 交易模块启动类
 */
@SpringBootApplication(scanBasePackages = "com.beaker.mintcraft.trade")
public class MintCraftTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MintCraftTradeApplication.class, args);
    }
}
