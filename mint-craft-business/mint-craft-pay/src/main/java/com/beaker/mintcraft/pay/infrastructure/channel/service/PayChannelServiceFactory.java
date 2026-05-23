package com.beaker.mintcraft.pay.infrastructure.channel.service;

import com.beaker.mintcraft.api.pay.constant.PayChannel;
import com.beaker.mintcraft.base.utils.BeanNameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.beaker.mintcraft.base.constant.ProfileConstant.PROFILE_DEV;

/**
 * @Author beaker
 * @Date 2026/5/23 16:31
 * @Description 支付服务渠道工厂
 */
@Service
public class PayChannelServiceFactory {

    @Autowired
    private final Map<String, PayChannelService> serviceMap = new ConcurrentHashMap<>();

    @Value("${spring.profiles.active}")
    private String profile;

    public PayChannelService get(PayChannel payChannel) {
        if (PROFILE_DEV.equals(profile)) {
            return serviceMap.get("mockPayChannelService");
        }

        // 组装出 beanName 获取对应的 bean
        String beanName = BeanNameUtils.getBeanName(payChannel.name(), "PayChannelService");
        PayChannelService payChannelService = serviceMap.get(beanName);

        if (payChannelService != null) {
            return payChannelService;
        } else {
            throw new UnsupportedOperationException(
                    "No PayChannelService Found With payChannel : " + payChannel + " , beanName : " + beanName);
        }
    }
}
