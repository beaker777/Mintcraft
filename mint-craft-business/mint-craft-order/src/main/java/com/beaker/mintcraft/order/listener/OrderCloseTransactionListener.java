package com.beaker.mintcraft.order.listener;

import com.alibaba.fastjson2.JSON;
import com.beaker.mintcraft.api.order.constant.TradeOrderEvent;
import com.beaker.mintcraft.api.order.constant.TradeOrderState;
import com.beaker.mintcraft.api.order.request.OrderCancelRequest;
import com.beaker.mintcraft.api.order.request.OrderTimeoutRequest;
import com.beaker.mintcraft.api.order.request.base.BaseOrderUpdateRequest;
import com.beaker.mintcraft.api.order.response.OrderResponse;
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import com.beaker.mintcraft.order.domain.service.OrderManageService;
import com.beaker.mintcraft.order.domain.service.OrderService;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author beaker
 * @Date 2026/5/21 22:34
 * @Description 订单关闭本地监听
 */
@Component
public class OrderCloseTransactionListener implements TransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderCloseTransactionListener.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderManageService orderManageService;

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        try {
            String closeType = msg.getProperties().get("CLOSE_TYPE");

            OrderResponse orderResponse = null;

            // 根据不同关单类型关单
            if (TradeOrderEvent.CANCEL.name().equals(closeType)) {
                OrderCancelRequest orderCancelRequest = JSON.parseObject(JSON.parseObject(msg.getBody()).getString("body"), OrderCancelRequest.class);
                orderResponse = orderManageService.cancel(orderCancelRequest);

                logger.info("executeLocalTransaction , baseOrderUpdateRequest = {} , closeType = {}", JSON.toJSONString(orderCancelRequest), closeType);
            } else if (TradeOrderEvent.TIME_OUT.name().equals(closeType)) {
                OrderTimeoutRequest orderTimeoutRequest = JSON.parseObject(JSON.parseObject(msg.getBody()).getString("body"), OrderTimeoutRequest.class);
                orderResponse = orderManageService.timeout(orderTimeoutRequest);

                logger.info("executeLocalTransaction , baseOrderUpdateRequest = {} , closeType = {}", JSON.toJSONString(orderTimeoutRequest), closeType);
            } else {
                throw new UnsupportedOperationException("unsupported closeType: " + closeType);
            }

            if (orderResponse.getSuccess()) {
                return LocalTransactionState.COMMIT_MESSAGE;
            } else {
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        } catch (Exception e) {
            logger.error("executeLocalTransaction error, message = {}", msg, e);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        String closeType = msg.getProperties().get("CLOSE_TYPE");

        // 根据不同关单类型确定是否关单成功
        BaseOrderUpdateRequest baseOrderUpdateRequest = null;
        if (TradeOrderEvent.CANCEL.name().equals(closeType)) {
            baseOrderUpdateRequest = JSON.parseObject(JSON.parseObject(new String(msg.getBody())).getString("body"), OrderCancelRequest.class);
        } else if (TradeOrderEvent.TIME_OUT.name().equals(closeType)) {
            baseOrderUpdateRequest = JSON.parseObject(JSON.parseObject(new String(msg.getBody())).getString("body"), OrderTimeoutRequest.class);
        } else {
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }

        TradeOrder tradeOrder = orderService.getOrder(baseOrderUpdateRequest.getOrderId());

        // 如果订单关单成功, 提交, 否则回滚
        if (tradeOrder.getOrderState() == TradeOrderState.CLOSED) {
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        return LocalTransactionState.ROLLBACK_MESSAGE;
    }
}
