package com.beaker.mintcraft.inventory.domain.domain.service.impl;

import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.response.InventoryResponse;
import com.beaker.mintcraft.inventory.domain.domain.service.InventoryService;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.beaker.mintcraft.base.response.ResponseCode.DUPLICATED;

/**
 * @Author beaker
 * @Date 2026/5/10 21:14
 * @Description 库存服务模板实现类
 */
public abstract class AbstractInventoryServiceImpl implements InventoryService {

    @Autowired
    private RedissonClient redissonClient;

    public static final String ERROR_CODE_INVENTORY_NOT_ENOUGH = "INVENTORY_NOT_ENOUGH";
    public static final String ERROR_CODE_INVENTORY_IS_ZERO = "INVENTORY_IS_ZERO";
    public static final String ERROR_CODE_KEY_NOT_FOUND = "KEY_NOT_FOUND";
    public static final String ERROR_CODE_OPERATION_ALREADY_EXECUTED = "OPERATION_ALREADY_EXECUTED";

    @Override
    public InventoryResponse init(InventoryRequest request) {
        InventoryResponse inventoryResponse = new InventoryResponse();

        // 幂等校验
        if (redissonClient.getBucket(getCacheKey(request)).isExists()) {
            inventoryResponse.setSuccess(true);
            inventoryResponse.setResponseCode(DUPLICATED.name());
            return inventoryResponse;
        }

        // 如果没初始化过, 设置库存
        redissonClient.getBucket(getCacheKey(request)).set(request.getInventory());
        inventoryResponse.setSuccess(true);
        inventoryResponse.setGoodsId(request.getGoodsId());
        inventoryResponse.setGoodsType(request.getGoodsType());
        inventoryResponse.setIdentifier(request.getIdentifier());
        inventoryResponse.setInventory(request.getInventory());
        return inventoryResponse;
    }

    @Override
    public Integer getInventory(InventoryRequest request) {
        return (Integer) redissonClient.getBucket(getCacheKey(request), IntegerCodec.INSTANCE).get();
    }

    public abstract String getCacheKey(InventoryRequest request);
}
