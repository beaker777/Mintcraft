package com.beaker.mintcraft.api.chain.service;

import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainCreateData;
import com.beaker.mintcraft.api.chain.response.data.ChainOperationData;

/**
 * @Author beaker
 * @Date 2026/5/24 17:54
 * @Description 链模块 facade 层接口
 */
public interface ChainFacadeService {

    /**
     * 创建链账户
     *
     * @param request
     * @return
     */
    ChainProcessResponse<ChainCreateData> createAddr(ChainProcessRequest request);

    /**
     * 铸造藏品
     *
     * @param request
     * @return
     */
    ChainProcessResponse<ChainOperationData> mint(ChainProcessRequest request);

}
