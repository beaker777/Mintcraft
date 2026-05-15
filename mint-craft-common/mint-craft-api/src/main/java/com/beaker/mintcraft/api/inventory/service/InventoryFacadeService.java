package com.beaker.mintcraft.api.inventory.service;

import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.base.response.SingleResponse;

/**
 * @Author beaker
 * @Date 2026/5/10 21:03
 * @Description 库存 facade 层接口
 */
public interface InventoryFacadeService {

    /**
     * 库存初始化
     *
     * @param inventoryRequest
     * @return
     */
    public SingleResponse<Boolean> init(InventoryRequest inventoryRequest);

    /**
     * 查询库存
     *
     * @param inventoryRequest
     * @return
     */
    public SingleResponse<Integer> queryInventory(InventoryRequest inventoryRequest);

    /**
     * 库存扣减
     *
     * @param inventoryRequest
     * @return
     */
    public SingleResponse<Boolean> decrease(InventoryRequest inventoryRequest);
}
