package com.beaker.mintcraft.collection.facade;

import com.alibaba.fastjson2.JSON;
import com.beaker.mintcraft.api.chain.constant.ChainOperateBizType;
import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainOperationData;
import com.beaker.mintcraft.api.chain.service.ChainFacadeService;
import com.beaker.mintcraft.api.collection.constant.CollectionInventoryModifyType;
import com.beaker.mintcraft.api.collection.request.admin.CollectionCreateRequest;
import com.beaker.mintcraft.api.collection.request.admin.CollectionModifyInventoryRequest;
import com.beaker.mintcraft.api.collection.request.admin.CollectionModifyPriceRequest;
import com.beaker.mintcraft.api.collection.request.admin.CollectionRemoveRequest;
import com.beaker.mintcraft.api.collection.response.CollectionChainResponse;
import com.beaker.mintcraft.api.collection.response.CollectionInventoryModifyResponse;
import com.beaker.mintcraft.api.collection.response.CollectionModifyResponse;
import com.beaker.mintcraft.api.collection.response.CollectionRemoveResponse;
import com.beaker.mintcraft.api.collection.service.CollectionManageFacadeService;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.collection.domain.entity.Collection;
import com.beaker.mintcraft.collection.domain.service.CollectionService;
import com.beaker.mintcraft.collection.infrastructure.exception.CollectionException;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.beaker.mintcraft.collection.infrastructure.exception.CollectionErrorCode.COLLECTION_INVENTORY_UPDATE_FAILED;

/**
 * @Author beaker
 * @Date 2026/5/31 21:06
 * @Description 藏品管理 facade 层实现类
 */
@DubboService
public class CollectionManageFacadeServiceImpl implements CollectionManageFacadeService {

    @DubboReference
    private ChainFacadeService chainFacadeService;

    @DubboReference
    private InventoryFacadeService inventoryFacadeService;

    @Resource
    private CollectionService collectionService;

    private static final Logger logger = LoggerFactory.getLogger(CollectionManageFacadeServiceImpl.class);

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

    @Override
    public CollectionModifyResponse modifyInventory(CollectionModifyInventoryRequest request) {
        CollectionModifyResponse response = new CollectionModifyResponse();
        response.setCollectionId(response.getCollectionId());

        CollectionInventoryModifyResponse modifyResponse = collectionService.modifyInventory(request);

        if (!modifyResponse.getSuccess()) {
            response.setSuccess(false);
            response.setResponseCode(COLLECTION_INVENTORY_UPDATE_FAILED.getCode());
            response.setResponseMessage(COLLECTION_INVENTORY_UPDATE_FAILED.getMessage());

            return response;
        }

        // 未修改库存则无需更新缓存
        if (modifyResponse.getModifyType() == CollectionInventoryModifyType.UNMODIFIED) {
            response.setSuccess(true);
            return response;
        }

        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setGoodsId(request.getCollectionId().toString());
        inventoryRequest.setGoodsType(GoodsType.COLLECTION);
        inventoryRequest.setIdentifier(request.getIdentifier());
        inventoryRequest.setInventory(modifyResponse.getQuantityModified());

        // 更新缓存
        SingleResponse<Boolean> inventoryResponse;
        if (modifyResponse.getModifyType() == CollectionInventoryModifyType.INCREASE) {
            inventoryResponse = inventoryFacadeService.increase(inventoryRequest);
        } else {
            inventoryResponse = inventoryFacadeService.decrease(inventoryRequest);
        }

        if (!inventoryResponse.getSuccess()) {
            logger.error("modify inventory failed: " + JSON.toJSONString(inventoryResponse));
            throw new CollectionException(COLLECTION_INVENTORY_UPDATE_FAILED);
        }

        response.setSuccess(true);
        return response;
    }

    @Override
    public CollectionModifyResponse modifyPrice(CollectionModifyPriceRequest request) {
        Boolean result = collectionService.modifyPrice(request);

        CollectionModifyResponse response = new CollectionModifyResponse();
        response.setSuccess(result);
        response.setCollectionId(request.getCollectionId());

        return response;
    }

    @Override
    public CollectionRemoveResponse remove(CollectionRemoveRequest request) {
        CollectionRemoveResponse response = new CollectionRemoveResponse();

        Boolean result = collectionService.remove(request);
        if (result) {
            // 更新数据库成功, 清除缓存
            InventoryRequest inventoryRequest = new InventoryRequest();
            inventoryRequest.setGoodsId(request.getCollectionId().toString());
            inventoryRequest.setGoodsType(GoodsType.COLLECTION);

            inventoryFacadeService.invalid(inventoryRequest);
        }

        response.setSuccess(result);
        response.setCollectionId(request.getCollectionId());
        return response;
    }
}
