package com.beaker.mintcraft.trade.infrastructure.config;

import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.pay.service.PayFacadeService;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/5/16 18:52
 * @Description 交易模块 dubbo 配置类
 */
@Configuration
public class TradeDubboConfiguration {

    @DubboReference
    private GoodsFacadeService goodsFacadeService;

    @DubboReference
    private OrderFacadeService orderFacadeService;

    @DubboReference
    private InventoryFacadeService inventoryFacadeService;

    @DubboReference
    private UserFacadeService userFacadeService;

    @DubboReference
    private PayFacadeService payFacadeService;

    @Bean
    @ConditionalOnMissingBean(name = "goodsFacadeService")
    public GoodsFacadeService goodsFacadeService() {
        return goodsFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "orderFacadeService")
    public OrderFacadeService orderFacadeService() {
        return orderFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "inventoryFacadeService")
    public InventoryFacadeService inventoryFacadeService() {
        return inventoryFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "payFacadeService")
    public PayFacadeService payFacadeService() {
        return payFacadeService;
    }
}
