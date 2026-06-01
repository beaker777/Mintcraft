package com.beaker.mintcraft.collection.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.beaker.mintcraft.api.collection.request.admin.CollectionCreateRequest;
import com.beaker.mintcraft.api.collection.request.admin.CollectionModifyInventoryRequest;
import com.beaker.mintcraft.api.collection.request.admin.CollectionModifyPriceRequest;
import com.beaker.mintcraft.api.collection.request.admin.CollectionRemoveRequest;
import com.beaker.mintcraft.api.collection.response.CollectionInventoryModifyResponse;
import com.beaker.mintcraft.api.goods.request.GoodsCancelSaleRequest;
import com.beaker.mintcraft.api.goods.request.GoodsTrySaleRequest;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.collection.domain.entity.Collection;

/**
 * @Author beaker
 * @Date 2026/5/10 18:01
 * @Description 藏品服务接口
 */
public interface CollectionService extends IService<Collection> {

    /**
     * 创建
     *
     * @param request
     * @return
     */
    public Collection create(CollectionCreateRequest request);

    /**
     * 更新库存
     *
     * @param request
     * @return
     */
    public CollectionInventoryModifyResponse modifyInventory(CollectionModifyInventoryRequest request);

    /**
     * 更新价格
     *
     * @param request
     * @return
     */
    public Boolean modifyPrice(CollectionModifyPriceRequest request);

    /**
     * 下架
     *
     * @param request
     * @return
     */
    public Boolean remove(CollectionRemoveRequest request);

    /**
     * 查询
     *
     * @param collectionId
     * @return
     */
    public Collection queryById(Long collectionId);

    /**
     * 分页查询
     *
     * @param keyWord
     * @param state
     * @param currentPage
     * @param pageSize
     * @return
     */
    public PageResponse<Collection> pageQueryByState(String keyWord, String state, int currentPage, int pageSize);

    /**
     * 售卖
     *
     * @param request
     * @return
     */
    public Boolean sale(GoodsTrySaleRequest request);


    /**
     * 取消
     *
     * @param request
     * @return
     */
    public Boolean cancel(GoodsCancelSaleRequest request);
}
