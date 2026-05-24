package com.beaker.mintcraft.chain.domain.service;

import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainCreateData;

/**
 * @Author beaker
 * @Date 2026/5/24 18:11
 * @Description 链服务接口
 */
public interface ChainService {

    /**
     * 创建交易链地址
     *
     * @param request
     * @return
     */
    ChainProcessResponse<ChainCreateData> createAddr(ChainProcessRequest request);
}
