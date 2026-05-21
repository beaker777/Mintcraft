package com.beaker.mintcraft.api.inventory.service;

import com.beaker.mintcraft.api.inventory.request.InventoryCheckRequest;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.response.InventoryCheckResponse;
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

    /**
     * 库存增加
     *
     * @param inventoryRequest
     * @return
     */
    public SingleResponse<Boolean> increase(InventoryRequest inventoryRequest);

    /**
     * 库存核对
     *
     * @param request
     * @return
     */
    public InventoryCheckResponse check(InventoryCheckRequest request);

    /**
     * 移除流水
     *
     * @param inventoryRequest
     * @return
     */
    public SingleResponse<Long> removeInventoryDecreaseLog(InventoryRequest inventoryRequest);

    /**
     * 查询库存扣减流水
     *
     * @param inventoryRequest
     * @return
     */
    public SingleResponse<String> getInventoryDecreaseLog(InventoryRequest inventoryRequest);


    /**
     * 查询库存增加流水
     *
     * @param inventoryRequest
     * @return
     */
    public SingleResponse<String> getInventoryIncreaseLog(InventoryRequest inventoryRequest);
}
