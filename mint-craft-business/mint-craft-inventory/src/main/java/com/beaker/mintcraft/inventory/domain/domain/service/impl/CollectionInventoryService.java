package com.beaker.mintcraft.inventory.domain.domain.service.impl;

import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import org.springframework.stereotype.Service;

/**
 * @Author beaker
 * @Date 2026/5/10 21:17
 * @Description 藏品模块库存查询实现类
 */
@Service
public class CollectionInventoryService extends AbstractInventoryServiceImpl {

    private static final String INVENTORY_KEY = "clc:inventory:";

    @Override
    public String getCacheKey(InventoryRequest request) {
        return INVENTORY_KEY + request.getGoodsId();
    }
}
