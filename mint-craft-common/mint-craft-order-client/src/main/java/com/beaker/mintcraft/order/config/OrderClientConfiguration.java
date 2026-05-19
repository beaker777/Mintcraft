package com.beaker.mintcraft.order.config;

import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import com.beaker.mintcraft.order.sharding.id.WorkerIdHolder;
import com.beaker.mintcraft.order.validator.OrderCreateValidator;
import com.beaker.mintcraft.order.validator.impl.GoodsValidator;
import com.beaker.mintcraft.order.validator.impl.UserValidator;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @Author beaker
 * @Date 2026/5/13 15:31
 * @Description 富客户端配置类
 */
@Configuration
public class OrderClientConfiguration {

    @Bean
    public WorkerIdHolder workerIdHolder(RedissonClient redisson) {
        return new WorkerIdHolder(redisson);
    }

    @Bean
    @Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
    public UserValidator userValidator(UserFacadeService userFacadeService) {
        return new UserValidator(userFacadeService);
    }

    @Bean
    @Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
    public GoodsValidator goodsValidator(GoodsFacadeService goodsFacadeService) {
        return new GoodsValidator(goodsFacadeService);
    }

    @Bean
    public OrderCreateValidator orderValidatorChain(UserValidator userValidator, GoodsValidator goodsValidator) {
        userValidator.setNext(goodsValidator);

        return userValidator;
    }

    @Bean
    public OrderCreateValidator orderConfirmValidatorChain(UserValidator userValidator, GoodsValidator goodsValidator) {
        userValidator.setNext(goodsValidator);

        return userValidator;
    }
}
