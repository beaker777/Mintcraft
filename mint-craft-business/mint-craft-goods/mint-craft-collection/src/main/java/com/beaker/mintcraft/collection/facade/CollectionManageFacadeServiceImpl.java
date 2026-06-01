package com.beaker.mintcraft.collection.facade;

import com.beaker.mintcraft.api.chain.constant.ChainOperateBizType;
import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainOperationData;
import com.beaker.mintcraft.api.chain.service.ChainFacadeService;
import com.beaker.mintcraft.api.collection.request.admin.CollectionCreateRequest;
import com.beaker.mintcraft.api.collection.response.CollectionChainResponse;
import com.beaker.mintcraft.api.collection.service.CollectionManageFacadeService;
import com.beaker.mintcraft.collection.domain.entity.Collection;
import com.beaker.mintcraft.collection.domain.service.CollectionService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author beaker
 * @Date 2026/5/31 21:06
 * @Description 藏品管理 facade 层实现类
 */
@DubboService
public class CollectionManageFacadeServiceImpl implements CollectionManageFacadeService {

    @DubboReference
    private ChainFacadeService chainFacadeService;

    @Resource
    private CollectionService collectionService;

    @Override
    public CollectionChainResponse create(CollectionCreateRequest request) {
        // 创建藏品
        Collection collection = collectionService.create(request);

        ChainProcessRequest chainProcessRequest = new ChainProcessRequest();
        chainProcessRequest.setIdentifier(request.getIdentifier());
        chainProcessRequest.setClassId(String.valueOf(collection.getId()));
        chainProcessRequest.setClassName(request.getName());
        chainProcessRequest.setBizType(ChainOperateBizType.COLLECTION.name());
        chainProcessRequest.setBizId(collection.getId().toString());

        // 藏品上链
        ChainProcessResponse<ChainOperationData> chainRes = chainFacadeService.chain(chainProcessRequest);

        CollectionChainResponse response = new CollectionChainResponse();
        if (!chainRes.getSuccess()) {
            response.setSuccess(false);
            return response;
        }
        response.setSuccess(true);
        response.setCollectionId(collection.getId());

        return response;
    }
}
