package com.beaker.mintcraft.trade.infrastructure.config;

import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
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

    @Bean
    @ConditionalOnMissingBean(name = "goodsFacadeService")
    public GoodsFacadeService goodsFacadeService() {
        return goodsFacadeService;
    }
}
