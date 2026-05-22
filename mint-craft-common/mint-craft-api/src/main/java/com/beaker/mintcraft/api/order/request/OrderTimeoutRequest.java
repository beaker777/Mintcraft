package com.beaker.mintcraft.api.order.request;

import com.beaker.mintcraft.api.order.constant.TradeOrderEvent;
import com.beaker.mintcraft.api.order.request.base.BaseOrderUpdateRequest;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/22 20:07
 * @Description 订单超时请求
 */
@Data
public class OrderTimeoutRequest extends BaseOrderUpdateRequest {

    @Override
    public TradeOrderEvent getOrderEvent() {
        return TradeOrderEvent.TIME_OUT;
    }
}
