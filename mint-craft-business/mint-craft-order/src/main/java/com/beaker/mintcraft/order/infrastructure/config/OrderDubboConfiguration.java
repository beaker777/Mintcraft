package com.beaker.mintcraft.order.infrastructure.config;

import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.api.pay.service.PayFacadeService;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author beaker
 * @Date 2026/5/15 16:50
 * @Description 订单模块 dubbo 配置类
 */
@Configuration
public class OrderDubboConfiguration {

    @DubboReference
    private UserFacadeService userFacadeService;

    @DubboReference
    private InventoryFacadeService inventoryFacadeService;

    @DubboReference
    private GoodsFacadeService goodsFacadeService;

    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return this.userFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "inventoryFacadeService")
    public InventoryFacadeService inventoryFacadeService() {
        return inventoryFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "goodsFacadeService")
    public GoodsFacadeService goodsFacadeService() {
        return goodsFacadeService;
    }
}
