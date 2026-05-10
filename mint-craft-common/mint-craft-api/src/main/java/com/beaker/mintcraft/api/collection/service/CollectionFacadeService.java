package com.beaker.mintcraft.api.collection.service;

import com.beaker.mintcraft.api.collection.valobj.CollectionVO;
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
}