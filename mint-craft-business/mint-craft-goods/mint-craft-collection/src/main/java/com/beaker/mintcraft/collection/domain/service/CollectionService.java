package com.beaker.mintcraft.collection.domain.service;

import com.beaker.mintcraft.api.goods.request.GoodsTrySaleRequest;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.collection.domain.entity.Collection;

/**
 * @Author beaker
 * @Date 2026/5/10 18:01
 * @Description 藏品服务接口
 */
public interface CollectionService {

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

}
