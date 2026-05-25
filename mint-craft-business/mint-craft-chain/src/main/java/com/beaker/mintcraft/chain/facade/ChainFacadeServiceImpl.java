package com.beaker.mintcraft.chain.facade;

import com.beaker.mintcraft.api.chain.constant.ChainType;
import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainCreateData;
import com.beaker.mintcraft.api.chain.response.data.ChainOperationData;
import com.beaker.mintcraft.api.chain.service.ChainFacadeService;
import com.beaker.mintcraft.chain.domain.service.ChainService;
import com.beaker.mintcraft.chain.domain.service.factory.ChainServiceFactory;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static com.beaker.mintcraft.base.constant.ProfileConstant.PROFILE_DEV;

/**
 * @Author beaker
 * @Date 2026/5/24 17:55
 * @Description 链模块 facade 层实现类
 */
@DubboService
public class ChainFacadeServiceImpl implements ChainFacadeService {

    @Value("${mintcraft.chain.type:MOCK}")
    private String chainType;

    @Value("${spring.profiles.active}")
    private String profile;


    @Resource
    private ChainServiceFactory chainServiceFactory;

    @Override
    public ChainProcessResponse<ChainCreateData> createAddr(ChainProcessRequest request) {
        return getChainService().createAddr(request);
    }

    @Override
    public ChainProcessResponse<ChainOperationData> mint(ChainProcessRequest request) {
        return getChainService().mint(request);
    }

    private ChainService getChainService() {
        if (PROFILE_DEV.equals(profile)) {
            return chainServiceFactory.get(ChainType.MOCK);
        }

        return chainServiceFactory.get(ChainType.valueOf(chainType));
    }
}
