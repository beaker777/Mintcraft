package com.beaker.mintcraft.api.collection.service;

import com.beaker.mintcraft.api.collection.request.admin.CollectionCreateRequest;
import com.beaker.mintcraft.api.collection.response.CollectionChainResponse;

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
}
