package com.beaker.mintcraft.collection.domain.service;

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

}
