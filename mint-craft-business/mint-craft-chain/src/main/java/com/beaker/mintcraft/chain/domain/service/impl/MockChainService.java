package com.beaker.mintcraft.chain.domain.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.beaker.mintcraft.api.chain.constant.ChainOperateBizType;
import com.beaker.mintcraft.api.chain.constant.ChainOperateType;
import com.beaker.mintcraft.api.chain.constant.ChainType;
import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.request.ChainQueryRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainCreateData;
import com.beaker.mintcraft.api.chain.response.data.ChainOperationData;
import com.beaker.mintcraft.api.chain.response.data.ChainResultData;
import com.beaker.mintcraft.chain.domain.constant.ChainCode;
import com.beaker.mintcraft.chain.domain.constant.ChainOperateState;
import com.beaker.mintcraft.chain.domain.entity.ChainRequest;
import com.beaker.mintcraft.chain.domain.response.ChainResponse;
import com.beaker.mintcraft.chain.domain.service.AbstractChainService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @Author beaker
 * @Date 2026/5/24 18:12
 * @Description mock 链服务
 */
@Service
public class MockChainService extends AbstractChainService {

    @Override
    public ChainProcessResponse<ChainCreateData> createAddr(ChainProcessRequest request) {
        return new ChainProcessResponse.Builder<>()
                .responseCode(ChainCode.SUCCESS.name())
                .data(new ChainCreateData(
                        request.getIdentifier(), UUID.randomUUID().toString(), "mockBlockChainName", ChainType.MOCK.name()))
                .buildSuccess();
    }

    @Override
    public ChainProcessResponse<ChainOperationData> chain(ChainProcessRequest request) {
        if (StringUtils.equals(ChainOperateBizType.BLIND_BOX.name(), request.getBizType())) {
            return doPostExecute(request, ChainOperateType.BLIND_BOX_CHAIN, chainRequest -> {

            });
        } else {
            return doPostExecute(request, ChainOperateType.COLLECTION_CHAIN, chainRequest -> {

            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChainProcessResponse<ChainOperationData> mint(ChainProcessRequest request) {
        return doPostExecute(request, ChainOperateType.COLLECTION_MINT, chainRequest -> {

        });
    }

    @Override
    public ChainProcessResponse<ChainOperationData> destroy(ChainProcessRequest request) {
        return doPostExecute(request, ChainOperateType.COLLECTION_DESTROY, chainRequest -> {

        });
    }

    @Override
    public ChainProcessResponse<ChainResultData> queryChainResult(ChainQueryRequest request) {
        ChainProcessResponse<ChainResultData> response = new ChainProcessResponse<>();
        response.setSuccess(true);
        response.setResponseCode("200");
        response.setResponseMessage("SUCCESS");

        ChainResultData data = new ChainResultData();
        data.setTxHash(UUID.randomUUID().toString());
        data.setNftId("nftId");
        data.setState(ChainOperateState.SUCCEED.name());
        response.setData(data);

        return response;
    }

    @Override
    protected ChainResponse doPost(ChainRequest chainRequest) {
        ChainResponse chainResponse = new ChainResponse();

        chainResponse.setSuccess(true);

        JSONObject data = new JSONObject();
        data.put("success",true);
        data.put("chainType","mock");
        chainResponse.setData(data);

        return chainResponse;
    }

    @Override
    protected ChainResponse doDelete(ChainRequest chainRequest) {
        ChainResponse chainResponse = new ChainResponse();

        chainResponse.setSuccess(true);

        JSONObject data = new JSONObject();
        data.put("success",true);
        data.put("chainType","mock");
        chainResponse.setData(data);

        return chainResponse;
    }

    @Override
    protected ChainResponse doGetQuery(ChainRequest chainRequest) {
        ChainResponse chainResponse = new ChainResponse();

        chainResponse.setSuccess(true);

        JSONObject data = new JSONObject();
        data.put("success",true);
        data.put("chainType","mock");
        chainResponse.setData(data);

        return chainResponse;
    }

    @Override
    protected String chainType() {
        return ChainType.MOCK.name();
    }
}
