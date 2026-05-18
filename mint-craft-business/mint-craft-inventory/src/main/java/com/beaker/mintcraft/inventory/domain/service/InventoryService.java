package com.beaker.mintcraft.inventory.domain.service;

import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.response.InventoryResponse;
import org.springframework.stereotype.Service;

/**
 * @Author beaker
 * @Date 2026/5/10 21:12
 * @Description 库存服务接口
 */
@Service
public interface InventoryService {


    /**
     * 初始化藏品库存
     *
     * @param request
     * @return
     */
    public InventoryResponse init(InventoryRequest request);

    /**
     * 获取藏品库存
     *
     * @param request
     * @return
     */
    public Integer getInventory(InventoryRequest request);

    /**
     * 扣减藏品库存
     *
     * @param request
     * @return
     */
    public InventoryResponse decrease(InventoryRequest request);

    /**
     * 移除库存操作日志
     * @param request
     * @return
     */
    public Long removeInventoryDecreaseLog(InventoryRequest request);
}
