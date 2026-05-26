package com.beaker.mintcraft.order.listener;

import cn.hutool.core.lang.Assert;
import com.beaker.mintcraft.api.collection.constant.GoodsSaleBizType;
import com.beaker.mintcraft.api.collection.constant.HeldCollectionState;
import com.beaker.mintcraft.api.collection.model.HeldCollectionDTO;
import com.beaker.mintcraft.api.order.request.OrderFinishRequest;
import com.beaker.mintcraft.api.order.response.OrderResponse;
import com.beaker.mintcraft.api.user.constant.UserType;
import com.beaker.mintcraft.mq.consumer.AbstractStreamConsumer;
import com.beaker.mintcraft.mq.param.MessageBody;
import com.beaker.mintcraft.order.domain.service.OrderManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.lang.model.type.UnionType;
import java.util.Date;
import java.util.function.Consumer;

/**
 * @Author beaker
 * @Date 2026/5/26 19:17
 * @Description 持有藏品消息监听
 */
@Slf4j
@Component
public class HeldCollectionMsgListener extends AbstractStreamConsumer {

    @Autowired
    private OrderManageService orderManageService;

    @Bean
    Consumer<Message<MessageBody>> heldCollection() {
        return msg -> {
            HeldCollectionDTO heldCollectionDTO = getMessage(msg, HeldCollectionDTO.class);

            if (heldCollectionDTO.getState().equals(HeldCollectionState.ACTIVED.name()) &&
            !heldCollectionDTO.getBizType().equals(GoodsSaleBizType.AIR_DROP.name())) {
                String orderId = heldCollectionDTO.getBizNo();

                OrderFinishRequest orderFinishRequest = new OrderFinishRequest();
                orderFinishRequest.setIdentifier("order_confirm_" + heldCollectionDTO.getId());
                orderFinishRequest.setOrderId(orderId);
                orderFinishRequest.setOperator(UserType.PLATFORM.name());
                orderFinishRequest.setOperatorType(UserType.PLATFORM);
                orderFinishRequest.setOperateTime(new Date());

                OrderResponse response = orderManageService.finish(orderFinishRequest);
                Assert.isTrue(response.getSuccess(), "finish order failed");
            }
        };
    }
}
