package com.beaker.mintcraft.inventory.domain.service.impl;

import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.response.InventoryResponse;
import com.beaker.mintcraft.inventory.domain.service.InventoryService;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.client.codec.IntegerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.beaker.mintcraft.base.response.ResponseCode.BIZ_ERROR;
import static com.beaker.mintcraft.base.response.ResponseCode.DUPLICATED;

/**
 * @Author beaker
 * @Date 2026/5/10 21:14
 * @Description 库存服务模板实现类
 */
public abstract class AbstractInventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractInventoryServiceImpl.class);

    @Autowired
    private RedissonClient redissonClient;

    public static final String ERROR_CODE_INVENTORY_NOT_ENOUGH = "INVENTORY_NOT_ENOUGH";
    public static final String ERROR_CODE_INVENTORY_IS_ZERO = "INVENTORY_IS_ZERO";
    public static final String ERROR_CODE_KEY_NOT_FOUND = "KEY_NOT_FOUND";
    public static final String ERROR_CODE_OPERATION_ALREADY_EXECUTED = "OPERATION_ALREADY_EXECUTED";

    public String decreaseScript;

    @PostConstruct
    public void initLuaScript() {
        ClassPathResource resource = new ClassPathResource("lua/decrease.lua");

        try {
            decreaseScript = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("读取 lua 脚本失败", e);
        }
    }

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

    @Override
    public InventoryResponse decrease(InventoryRequest request) {
        InventoryResponse inventoryResponse = new InventoryResponse();

        try {
            // 调用 lua 脚本扣减库存
            Integer result = ((Long) redissonClient.getScript().eval(
                    RScript.Mode.READ_WRITE,
                    decreaseScript,
                    RScript.ReturnType.INTEGER,
                    Arrays.asList(getCacheKey(request), getCacheStreamKey(request)),
                    request.getInventory(), "DECREASE_" + request.getIdentifier()
                )).intValue();

            inventoryResponse.setSuccess(true);
            inventoryResponse.setGoodsId(request.getGoodsId());
            inventoryResponse.setGoodsType(request.getGoodsType());
            inventoryResponse.setIdentifier(request.getIdentifier());
            inventoryResponse.setInventory(result);
            return inventoryResponse;
        } catch (RedisException e) {
            logger.error("decrease error, goodsId = {}, identifier = {}", request.getGoodsId(), request.getIdentifier(), e);
            inventoryResponse.setSuccess(false);
            inventoryResponse.setGoodsId(request.getGoodsId());
            inventoryResponse.setGoodsType(request.getGoodsType());
            inventoryResponse.setIdentifier(request.getIdentifier());

            // 根据不同情况设置 errorCode
            if (e.getMessage().startsWith(ERROR_CODE_INVENTORY_NOT_ENOUGH)) {
                inventoryResponse.setResponseCode(ERROR_CODE_INVENTORY_NOT_ENOUGH);
            } else if (e.getMessage().startsWith(ERROR_CODE_INVENTORY_IS_ZERO)) {
                inventoryResponse.setResponseCode(ERROR_CODE_INVENTORY_IS_ZERO);
            } else if (e.getMessage().startsWith(ERROR_CODE_KEY_NOT_FOUND)) {
                inventoryResponse.setResponseCode(ERROR_CODE_KEY_NOT_FOUND);
            } else if (e.getMessage().startsWith(ERROR_CODE_OPERATION_ALREADY_EXECUTED)) {
                inventoryResponse.setResponseCode(ERROR_CODE_OPERATION_ALREADY_EXECUTED);
                inventoryResponse.setSuccess(true);
            } else {
                inventoryResponse.setResponseCode(BIZ_ERROR.name());
            }
            inventoryResponse.setResponseMessage(e.getMessage());

            return inventoryResponse;
        }
    }

    /**
     * 获取库存缓存的 key
     *
     * @param request
     * @return
     */
    public abstract String getCacheKey(InventoryRequest request);

    /**
     * 获取库存流水缓存的 key
     * @param request
     * @return
     */
    protected abstract String getCacheStreamKey(InventoryRequest request);
}
