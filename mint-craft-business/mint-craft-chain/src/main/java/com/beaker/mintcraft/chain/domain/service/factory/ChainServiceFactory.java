package com.beaker.mintcraft.chain.domain.service.factory;

import com.beaker.mintcraft.api.chain.constant.ChainType;
import com.beaker.mintcraft.base.utils.BeanNameUtils;
import com.beaker.mintcraft.chain.domain.service.ChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author beaker
 * @Date 2026/5/24 18:27
 * @Description 链服务工厂
 */
@Service
public class ChainServiceFactory {

    @Autowired
    private final Map<String, ChainService> chainServiceMap = new ConcurrentHashMap<>();

    public ChainService get(ChainType chainType) {
        // 组装出 beanName 获取对应的类
        String beanName = BeanNameUtils.getBeanName(chainType.name(), "ChainService");
        ChainService chainService = chainServiceMap.get(beanName);

        if (chainService != null) {
            return chainService;
        } else {
            throw new UnsupportedOperationException("No ChainService Found With chainType : " + chainType);
        }
    }
}
