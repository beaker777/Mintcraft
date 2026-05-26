package com.beaker.mintcraft.goods.listener;

import cn.hutool.core.lang.Assert;
import com.beaker.mintcraft.api.chain.model.ChainOperateBody;
import com.beaker.mintcraft.api.chain.response.data.ChainResultData;
import com.beaker.mintcraft.api.collection.request.held.HeldCollectionActiveRequest;
import com.beaker.mintcraft.collection.domain.service.impl.HeldCollectionService;
import com.beaker.mintcraft.mq.consumer.AbstractStreamConsumer;
import com.beaker.mintcraft.mq.param.MessageBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


/**
 * @Author beaker
 * @Date 2026/5/26 17:46
 * @Description 链操作结果监听器
 */
@Slf4j
@Component
public class ChainOperateResultListener extends AbstractStreamConsumer {

    @Autowired
    private HeldCollectionService heldCollectionService;

    @Bean
    Consumer<Message<MessageBody>> chain() {
        return msg -> {
            ChainOperateBody chainOperateBody = getMessage(msg, ChainOperateBody.class);
            ChainResultData chainResultData = chainOperateBody.getChainResultData();

            boolean result;

            // 成功情况处理
            switch (chainOperateBody.getOperateType()) {
                case COLLECTION_MINT:
                    HeldCollectionActiveRequest request = new HeldCollectionActiveRequest();
                    request.setHeldCollectionId(chainOperateBody.getBizId());
                    request.setIdentifier(chainOperateBody.getOperateInfoId().toString());
                    request.setNftId(chainResultData.getNftId());
                    request.setTxHash(chainResultData.getTxHash());

                    result  = heldCollectionService.active(request);
                    Assert.isTrue(result, "active held collection failed");

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + chainOperateBody.getBizType().name());
            }
        };
    }
}
