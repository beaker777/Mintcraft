package com.beaker.mintcraft.api.collection.service;

import com.beaker.mintcraft.api.collection.request.admin.*;
import com.beaker.mintcraft.api.collection.response.CollectionAirdropResponse;
import com.beaker.mintcraft.api.collection.response.CollectionChainResponse;
import com.beaker.mintcraft.api.collection.response.CollectionModifyResponse;
import com.beaker.mintcraft.api.collection.response.CollectionRemoveResponse;

/**
 * @Author beaker
 * @Date 2026/5/31 21:06
 * @Description 藏品管理 facade 层接口
 */
public interface CollectionManageFacadeService {

    /**
     * 创建藏品
     *
     * @param request
     * @return
     */
    public CollectionChainResponse create(CollectionCreateRequest request);


    /**
     * 藏品库存修改
     *
     * @param request
     * @return
     */
    public CollectionModifyResponse modifyInventory(CollectionModifyInventoryRequest request);

    /**
     * 藏品价格修改
     *
     * @param request
     * @return
     */
    public CollectionModifyResponse modifyPrice(CollectionModifyPriceRequest request);

    /**
     * 藏品下架
     *
     * @param request
     * @return
     */
    public CollectionRemoveResponse remove(CollectionRemoveRequest request);

    /**
     * 空投
     *
     * @param request
     * @return
     */
    public CollectionAirdropResponse airDrop(CollectionAirDropRequest request);
}
