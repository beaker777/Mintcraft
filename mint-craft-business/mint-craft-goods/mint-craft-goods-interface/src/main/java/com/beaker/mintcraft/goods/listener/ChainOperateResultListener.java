package com.beaker.mintcraft.goods.listener;

import cn.hutool.core.lang.Assert;
import com.beaker.mintcraft.api.chain.model.ChainOperateBody;
import com.beaker.mintcraft.api.chain.response.data.ChainResultData;
import com.beaker.mintcraft.api.collection.constant.CollectionState;
import com.beaker.mintcraft.api.collection.request.held.HeldCollectionActiveRequest;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.base.exception.biz.BizException;
import com.beaker.mintcraft.base.exception.biz.RepoErrorCode;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.collection.domain.entity.Collection;
import com.beaker.mintcraft.collection.domain.service.CollectionService;
import com.beaker.mintcraft.collection.domain.service.impl.HeldCollectionService;
import com.beaker.mintcraft.collection.infrastructure.exception.CollectionException;
import com.beaker.mintcraft.mq.consumer.AbstractStreamConsumer;
import com.beaker.mintcraft.mq.param.MessageBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Consumer;

import static com.beaker.mintcraft.collection.infrastructure.exception.CollectionErrorCode.COLLECTION_QUERY_FAIL;


/**
 * @Author beaker
 * @Date 2026/5/26 17:46
 * @Description 链操作结果监听器
 */
@Slf4j
@Component
public class ChainOperateResultListener extends AbstractStreamConsumer {

    @DubboReference
    private InventoryFacadeService inventoryFacadeService;

    @Autowired
    private HeldCollectionService heldCollectionService;

    @Autowired
    private CollectionService collectionService;

    @Bean
    Consumer<Message<MessageBody>> chain() {
        return msg -> {
            ChainOperateBody chainOperateBody = getMessage(msg, ChainOperateBody.class);
            ChainResultData chainResultData = chainOperateBody.getChainResultData();

            boolean result;

            // 成功情况处理
            switch (chainOperateBody.getOperateType()) {
                case COLLECTION_MINT:
                    // 用户持有藏品发放, 状态推进到 active
                    HeldCollectionActiveRequest request = new HeldCollectionActiveRequest();
                    request.setHeldCollectionId(chainOperateBody.getBizId());
                    request.setIdentifier(chainOperateBody.getOperateInfoId().toString());
                    request.setNftId(chainResultData.getNftId());
                    request.setTxHash(chainResultData.getTxHash());

                    result  = heldCollectionService.active(request);
                    Assert.isTrue(result, "active held collection failed");

                    break;
                case COLLECTION_CHAIN:
                    // 藏品上链成功
                    Collection collection = collectionService.getById(chainOperateBody.getBizId());
                    if (collection == null) {
                        throw new CollectionException(COLLECTION_QUERY_FAIL);
                    }

                    // 先写缓存, 成功再更新状态
                    initInventory(collection.getId().toString(), GoodsType.COLLECTION, collection.getQuantity(), collection.getId().toString());

                    // 更新状态
                    collection.setState(CollectionState.SUCCEED);
                    collection.setSyncChainTime(new Date());
                    result = collectionService.updateById(collection);
                    Assert.isTrue(result, () -> new BizException(RepoErrorCode.UPDATE_FAILED));

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + chainOperateBody.getBizType().name());
            }
        };
    }

    private void initInventory(String goodsId, GoodsType goodsType, int inventory, String identifier) {
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setGoodsId(goodsId);
        inventoryRequest.setGoodsType(goodsType);
        inventoryRequest.setInventory(inventory);
        inventoryRequest.setIdentifier(identifier);

        SingleResponse<Boolean> inventoryResponse = inventoryFacadeService.init(inventoryRequest);

        if (!inventoryResponse.getSuccess()) {
            throw new BizException(RepoErrorCode.UPDATE_FAILED);
        }
    }
}
