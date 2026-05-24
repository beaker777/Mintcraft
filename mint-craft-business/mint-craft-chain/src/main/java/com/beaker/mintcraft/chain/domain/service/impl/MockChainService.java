package com.beaker.mintcraft.chain.domain.service.impl;

import com.beaker.mintcraft.api.chain.constant.ChainType;
import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainCreateData;
import com.beaker.mintcraft.chain.domain.constant.ChainCode;
import com.beaker.mintcraft.chain.domain.service.AbstractChainService;
import org.springframework.stereotype.Service;

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
}
