package com.beaker.mintcraft.pay.infrastructure.config;

import com.beaker.mintcraft.api.chain.service.ChainFacadeService;
import com.beaker.mintcraft.api.collection.service.CollectionFacadeService;
import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
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

    @DubboReference
    private OrderFacadeService orderFacadeService;

    @DubboReference
    private GoodsFacadeService goodsFacadeService;

    @DubboReference
    private CollectionFacadeService collectionFacadeService;

    @DubboReference
    private UserFacadeService userFacadeService;

    @DubboReference
    private ChainFacadeService chainFacadeService;

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

    @Bean
    @ConditionalOnMissingBean(name = "collectionFacadeService")
    public CollectionFacadeService collectionFacadeService() {
        return collectionFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "chainFacadeService")
    public ChainFacadeService chainFacadeService() {
        return chainFacadeService;
    }
}
