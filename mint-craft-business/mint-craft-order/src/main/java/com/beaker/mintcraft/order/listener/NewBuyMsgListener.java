package com.beaker.mintcraft.order.listener;

import com.alibaba.fastjson2.JSON;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.api.order.request.OrderCreateAndConfirmRequest;
import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.api.order.response.OrderResponse;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.user.constant.UserType;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.mq.consumer.AbstractStreamConsumer;
import com.beaker.mintcraft.mq.param.MessageBody;
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import com.beaker.mintcraft.order.domain.service.OrderService;
import com.beaker.mintcraft.order.exception.OrderException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Consumer;

import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.INVENTORY_INCREASE_FAILED;
import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.ORDER_CREATE_VALID_FAILED;

/**
 * @Author beaker
 * @Date 2026/5/16 20:21
 * @Description buy 方法消息监听类
 *
 * 本类会在 rocketmq.broker.check 为 false 时启用, 是单条消息的消费者.
 * 不强制依赖 RocketMQ , 但是如果不部署 MQ 会无法发送和消费信息.
 */
@Component
@Slf4j
@ConditionalOnProperty(value = "rocketmq.broker.check", havingValue = "false", matchIfMissing = true)
public class NewBuyMsgListener extends AbstractStreamConsumer {

    @Resource
    private OrderService orderService;

    @Resource
    private OrderFacadeService orderFacadeService;

    @Resource
    private InventoryFacadeService inventoryFacadeService;

    @Bean
    Consumer<Message<MessageBody>> newBuy() {
        return msg -> {
            OrderCreateRequest orderCreateRequest = getMessage(msg, OrderCreateRequest.class);
            doNewBuyExecute(orderCreateRequest);
        };
    }

    public void doNewBuyExecute(OrderCreateRequest orderCreateRequest) {
        OrderCreateAndConfirmRequest orderCreateAndConfirmRequest = new OrderCreateAndConfirmRequest();
        BeanUtils.copyProperties(orderCreateRequest, orderCreateAndConfirmRequest);
        orderCreateAndConfirmRequest.setOperator(UserType.PLATFORM.name());
        orderCreateAndConfirmRequest.setOperatorType(UserType.PLATFORM);
        orderCreateAndConfirmRequest.setOperateTime(new Date());
        orderCreateAndConfirmRequest.setSyncDecreaseInventory(true);

        OrderResponse orderResponse = orderFacadeService.createAndConfirm(orderCreateAndConfirmRequest);
        if (!orderResponse.getSuccess() && ORDER_CREATE_VALID_FAILED.getCode().equals(orderResponse.getResponseCode())) {
            // 订单因为校验不通过导致下单失败, 回滚库存
            // fixme: 这里没有补偿其他类型的错误
            String orderId = orderResponse.getOrderId();
            TradeOrder tradeOrder = orderService.getOrder(orderId);

            // 重查一次, 避免出现并发情况
            if (tradeOrder == null) {
                InventoryRequest inventoryRequest = new InventoryRequest();
                inventoryRequest.setGoodsId(orderCreateRequest.getGoodsId());
                inventoryRequest.setGoodsType(orderCreateRequest.getGoodsType());
                inventoryRequest.setIdentifier(orderCreateRequest.getOrderId());
                inventoryRequest.setInventory(orderCreateRequest.getItemCount());

                SingleResponse<Boolean> increaseResponse = inventoryFacadeService.increase(inventoryRequest);
                if (increaseResponse.getSuccess()) {
                    log.info("increase success, inventoryRequest: {}", inventoryRequest);
                    // 库存回滚后提前返回
                    return;
                } else {
                    log.error("increase inventory failed, orderCreateRequest:{} , increaseResponse : {}", JSON.toJSONString(orderCreateRequest), JSON.toJSONString(increaseResponse));
                    throw new OrderException(INVENTORY_INCREASE_FAILED);
                }
            }
        }
    }
}
