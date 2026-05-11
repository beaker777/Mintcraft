package com.beaker.mintcraft.api.collection.service;

import com.beaker.mintcraft.api.collection.request.CollectionPageQueryRequest;
import com.beaker.mintcraft.api.collection.request.HeldCollectionPageQueryRequest;
import com.beaker.mintcraft.api.collection.valobj.CollectionVO;
import com.beaker.mintcraft.api.collection.valobj.HeldCollectionVO;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.base.response.SingleResponse;

/**
 * @Author beaker
 * @Date 2026/5/10 18:39
 * @Description 藏品 service 层接口
 */
public interface CollectionFacadeService {

    /**
     * 根据Id查询藏品
     *
     * @param collectionId
     * @return
     */
    public SingleResponse<CollectionVO> queryById(Long collectionId);

    /**
     * 藏品分页查询
     *
     * @param request
     * @return
     */
    public PageResponse<CollectionVO> pageQuery(CollectionPageQueryRequest request);

    /**
     * 持有藏品数量查询
     * @param userId
     * @return
     */
    public SingleResponse<Long> queryHeldCollectionCount(String userId);

    /**
     * 根据id查询持有藏品
     *
     * @param heldCollectionId
     * @return
     */
    public SingleResponse<HeldCollectionVO> queryHeldCollectionById(Long heldCollectionId);

    /**
     * 持有藏品分页查询
     *
     * @param request
     * @return
     */
    public PageResponse<HeldCollectionVO> pageQueryHeldCollection(HeldCollectionPageQueryRequest request);
}