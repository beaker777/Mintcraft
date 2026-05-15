package com.beaker.mintcraft.order.domain.listener.event;

import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import org.springframework.context.ApplicationEvent;

/**
 * @Author beaker
 * @Date 2026/5/15 19:55
 * @Description 订单创建任务
 */
public class OrderCreateEvent extends ApplicationEvent {

    public OrderCreateEvent(TradeOrder tradeOrder) {
        super(tradeOrder);
    }
}
