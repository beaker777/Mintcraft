package com.beaker.mintcraft.chain.domain.service;

import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.request.ChainQueryRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainCreateData;
import com.beaker.mintcraft.api.chain.response.data.ChainOperationData;
import com.beaker.mintcraft.api.chain.response.data.ChainResultData;
import com.beaker.mintcraft.chain.domain.entity.ChainOperateInfo;

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

    /**
     * 上链藏品
     *
     * @param request
     * @return
     */
    ChainProcessResponse<ChainOperationData> chain(ChainProcessRequest request);

    /**
     * 铸造藏品
     *
     * @param request
     * @return
     */
    ChainProcessResponse<ChainOperationData> mint(ChainProcessRequest request);

    /**
     * 销毁藏品
     *
     * @param request
     * @return
     */
    ChainProcessResponse<ChainOperationData> destroy(ChainProcessRequest request);

    /**
     * 查询上链交易结果
     *
     * @param request
     * @return
     */
    ChainProcessResponse<ChainResultData> queryChainResult(ChainQueryRequest request);

    /**
     * 发消息
     *
     * @param chainOperateInfo
     * @param chainResultData
     */
    public void sendMsg(ChainOperateInfo chainOperateInfo, ChainResultData chainResultData);

}
