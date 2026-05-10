package com.beaker.mintcraft.inventory.domain.domain.service;

import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import org.springframework.stereotype.Service;

/**
 * @Author beaker
 * @Date 2026/5/10 21:12
 * @Description 库存服务接口
 */
@Service
public interface InventoryService {

    /**
     * 获取藏品库存
     *
     * @param request
     * @return
     */
    public Integer getInventory(InventoryRequest request);
}
