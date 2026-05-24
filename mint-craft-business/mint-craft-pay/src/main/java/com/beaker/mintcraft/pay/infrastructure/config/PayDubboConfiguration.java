package com.beaker.mintcraft.pay.infrastructure.config;

import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/5/24 15:37
 * @Description pay 模块 dubbo 注入管理
 */
@Configuration
public class PayDubboConfiguration {

    @Resource
    private OrderFacadeService orderFacadeService;

    @Resource
    private GoodsFacadeService goodsFacadeService;

    @Bean
    @ConditionalOnMissingBean(name = "orderFacadeService")
    public OrderFacadeService orderFacadeService() {
        return orderFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "goodsFacadeService")
    public GoodsFacadeService goodsFacadeService() {
        return goodsFacadeService;
    }
}
