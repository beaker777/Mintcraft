package com.beaker.mintcraft.collection.facade;

import com.alibaba.fastjson2.JSON;
import com.beaker.mintcraft.api.chain.constant.ChainOperateBizType;
import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainOperationData;
import com.beaker.mintcraft.api.chain.service.ChainFacadeService;
import com.beaker.mintcraft.api.collection.constant.CollectionInventoryModifyType;
import com.beaker.mintcraft.api.collection.constant.CollectionState;
import com.beaker.mintcraft.api.collection.request.admin.*;
import com.beaker.mintcraft.api.collection.response.*;
import com.beaker.mintcraft.api.collection.service.CollectionManageFacadeService;
import com.beaker.mintcraft.api.collection.valobj.HeldCollectionVO;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.api.user.request.UserQueryRequest;
import com.beaker.mintcraft.api.user.response.UserQueryResponse;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.collection.domain.entity.Collection;
import com.beaker.mintcraft.collection.domain.service.CollectionService;
import com.beaker.mintcraft.collection.infrastructure.exception.CollectionException;
import com.beaker.mintcraft.rpc.support.RemoteCallWrapper;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.*;
import static com.beaker.mintcraft.base.response.ResponseCode.DUPLICATED;
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

    @DubboReference
    private UserFacadeService userFacadeService;

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

    @Override
    public CollectionAirdropResponse airDrop(CollectionAirDropRequest request) {
        // 检查用户是否可被空投, 这里设置的比较简单, 如果后续节点较多可以改成责任链
        UserQueryRequest userQueryRequest = new UserQueryRequest(request.getRecipientUserId());
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
        checkUser(userQueryResponse);

        // 检查藏品是否可被空投, 这里设置的比较简单, 如果后续节点较多可以改成责任链
        Collection collection = collectionService.queryById(request.getCollectionId());
        checkCollection(collection, request.getQuantity());

        CollectionAirdropResponse response = collectionService.airDrop(request, collection);

        // 执行失败或幂等成功无需上链, 直接返回
        if (!response.getSuccess() || response.getResponseCode().equals(DUPLICATED.name())) {
            return response;
        }

        for (HeldCollectionVO heldCollection : response.getHeldCollections()) {
            ChainProcessRequest chainProcessRequest = new ChainProcessRequest();
            chainProcessRequest.setRecipient(userQueryResponse.getData().getBlockChainUrl());
            chainProcessRequest.setClassId(String.valueOf(heldCollection.getCollectionId()));
            chainProcessRequest.setClassName(heldCollection.getName());
            chainProcessRequest.setSerialNo(heldCollection.getSerialNo());
            chainProcessRequest.setBizId(heldCollection.getId());
            chainProcessRequest.setBizType(ChainOperateBizType.HELD_COLLECTION.name());
            chainProcessRequest.setIdentifier(UUID.randomUUID().toString());

            // 如果失败了, 依靠定时任务补偿
            ChainProcessResponse<ChainOperationData> chainProcessResponse = RemoteCallWrapper
                    .call(req -> chainFacadeService.mint(chainProcessRequest), chainProcessRequest, "mint");

            response.setSuccess(chainProcessResponse.getSuccess());
        }

        return response;
    }

    private void checkUser(UserQueryResponse<UserInfo> userQueryResponse) {
        if (!userQueryResponse.getSuccess() || userQueryResponse.getData() == null) {
            throw new CollectionException(USER_NOT_EXIST);
        }

        UserInfo userInfo = userQueryResponse.getData();
        if (!userInfo.userCanBuy()) {
            throw new CollectionException(BUYER_STATUS_ABNORMAL);
        }
    }

    private void checkCollection(Collection collection, Integer quantity) {
        if (collection == null) {
            throw new CollectionException(COLLECTION_NO_EXIST);
        }

        if (collection.getState() != CollectionState.SUCCEED) {
            throw new CollectionException(GOODS_NOT_AVAILABLE);
        }

        if (collection.getSaleableInventory() < quantity) {
            throw new CollectionException(INVENTORY_NOT_ENOUGH);
        }
    }
}
