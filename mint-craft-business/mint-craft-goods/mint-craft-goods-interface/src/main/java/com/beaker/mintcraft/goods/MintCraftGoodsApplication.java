package com.beaker.mintcraft.goods;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author beaker
 * @Date 2026/5/10 20:07
 * @Description 商品模块启动类
 */
@SpringBootApplication(scanBasePackages = {"com.beaker.mintcraft.collection"})
@EnableDubbo(scanBasePackages = {"com.beaker.mintcraft.collection"})
public class MintCraftGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MintCraftGoodsApplication.class);
    }
}
