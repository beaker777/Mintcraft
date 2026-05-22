package com.beaker.mintcraft.trade.listener;

import com.alibaba.fastjson2.JSON;
import com.beaker.mintcraft.api.goods.request.GoodsSaleRequest;
import com.beaker.mintcraft.api.goods.response.GoodsSaleResponse;
import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.api.order.constant.TradeOrderEvent;
import com.beaker.mintcraft.api.order.constant.TradeOrderState;
import com.beaker.mintcraft.api.order.request.OrderCancelRequest;
import com.beaker.mintcraft.api.order.request.OrderTimeoutRequest;
import com.beaker.mintcraft.api.order.request.base.BaseOrderUpdateRequest;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.order.valobj.TradeOrderVO;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.mq.consumer.AbstractStreamConsumer;
import com.beaker.mintcraft.mq.param.MessageBody;
import com.beaker.mintcraft.trade.infrastructure.exception.TradeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static com.beaker.mintcraft.trade.infrastructure.exception.TradeErrorCode.INVENTORY_ROLLBACK_FAILED;

/**
 * @Author beaker
 * @Date 2026/5/21 22:14
 * @Description 订单监听器
 */
@Slf4j
@Component
public class TradeOrderListener extends AbstractStreamConsumer {

    @Autowired
    private OrderFacadeService orderFacadeService;

    @Autowired
    private GoodsFacadeService goodsFacadeService;

    @Autowired
    private InventoryFacadeService inventoryFacadeService;

    @Bean
    Consumer<Message<MessageBody>> orderClose() {
        return msg -> {
            String closeType = msg.getHeaders().get("CLOSE_TYPE", String.class);
            BaseOrderUpdateRequest orderUpdateRequest;

            // 根据关单情况, 获取 cancelRequest
            if (TradeOrderEvent.CANCEL.name().equals(closeType)) {
                orderUpdateRequest = getMessage(msg, OrderCancelRequest.class);
            } else if (TradeOrderEvent.TIME_OUT.name().equals(closeType)) {
                orderUpdateRequest = getMessage(msg, OrderTimeoutRequest.class);
            } else {
                throw new UnsupportedOperationException("unsupported closeType: {}" + closeType);
            }

            // 查询订单是否已经关闭
            SingleResponse<TradeOrderVO> response = orderFacadeService.getTradeOrder(orderUpdateRequest.getOrderId());
            if (!response.getSuccess()) {
                log.error("getTradeOrder failed, orderCloseRequest: {}, orderQueryResponse: {}", JSON.toJSONString(orderUpdateRequest), JSON.toJSONString(response));
                throw new TradeException(INVENTORY_ROLLBACK_FAILED);
            }
            TradeOrderVO tradeOrderVO = response.getData();
            if (tradeOrderVO.getOrderState() != TradeOrderState.CLOSED) {
                log.error("trade order state is illegal ,orderCloseRequest: {}, tradeOrderVO: {}", JSON.toJSONString(orderUpdateRequest), JSON.toJSONString(tradeOrderVO));
                throw new TradeException(INVENTORY_ROLLBACK_FAILED);
            }

            // 回滚藏品 MySQL 库存
            GoodsSaleRequest goodsSaleRequest = new GoodsSaleRequest(tradeOrderVO);
            GoodsSaleResponse cancelSaleResult = goodsFacadeService.cancelSale(goodsSaleRequest);
            if (!cancelSaleResult.getSuccess()) {
                log.error("cancelSale failed, orderCloseRequest:{}, collectionSaleResponse:{}", JSON.toJSONString(orderUpdateRequest), JSON.toJSONString(cancelSaleResult));
                throw new TradeException(INVENTORY_ROLLBACK_FAILED);
            }

            // 回滚藏品 Redis 库存
            InventoryRequest inventoryRequest = new InventoryRequest(tradeOrderVO);
            SingleResponse<Boolean> increaseResponse = inventoryFacadeService.increase(inventoryRequest);
            if (increaseResponse.getSuccess()) {
                log.info("increase success, collectionInventoryRequest:{}", inventoryRequest);
            } else {
                log.error("increase inventory failed, orderCloseRequest:{} , increaseResponse : {}", JSON.toJSONString(orderUpdateRequest), JSON.toJSONString(increaseResponse));
                throw new TradeException(INVENTORY_ROLLBACK_FAILED);
            }
        };
    }
}
