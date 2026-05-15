package com.beaker.mintcraft.inventory.domain.service.impl;

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

    private static final String INVENTORY_STREAM_KEY = "clc:inventory:stream:";

    @Override
    public String getCacheKey(InventoryRequest request) {
        return INVENTORY_KEY + request.getGoodsId();
    }

    @Override
    protected String getCacheStreamKey(InventoryRequest request) {
        return INVENTORY_STREAM_KEY + request.getGoodsId();
    }
}
