package com.beaker.mintcraft.order.domain.listener;

import com.beaker.mintcraft.api.order.request.OrderConfirmRequest;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.user.constant.UserType;
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import com.beaker.mintcraft.order.domain.listener.event.OrderCreateEvent;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/5/15 19:54
 * @Description 订单任务监听器
 */
@Component
public class OrderEventListener {

    @Resource
    private OrderFacadeService orderFacadeService;

    @TransactionalEventListener(value = OrderCreateEvent.class)
    public void onApplicationEvent(OrderCreateEvent event) {
        TradeOrder tradeOrder = (TradeOrder) event.getSource();

        // 监听到订单异步确认的任务后发起请求
        OrderConfirmRequest confirmRequest = new OrderConfirmRequest();
        confirmRequest.setOperator(UserType.PLATFORM.name());
        confirmRequest.setOperatorType(UserType.PLATFORM);
        confirmRequest.setOrderId(tradeOrder.getOrderId());
        confirmRequest.setIdentifier(tradeOrder.getIdentifier());
        confirmRequest.setOperateTime(new Date());
        confirmRequest.setBuyerId(tradeOrder.getBuyerId());
        confirmRequest.setItemCount(tradeOrder.getItemCount());
        confirmRequest.setGoodsType(tradeOrder.getGoodsType());
        confirmRequest.setGoodsId(tradeOrder.getGoodsId());

        // 确认订单
        orderFacadeService.confirm(confirmRequest);
    }
}
