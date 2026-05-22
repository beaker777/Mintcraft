package com.beaker.mintcraft.api.order.request;

import com.beaker.mintcraft.api.order.constant.TradeOrderEvent;
import com.beaker.mintcraft.api.order.request.base.BaseOrderUpdateRequest;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/21 21:56
 * @Description 订单取消请求
 */
@Data
public class OrderCancelRequest extends BaseOrderUpdateRequest {

    @Override
    public TradeOrderEvent getOrderEvent() {
        return TradeOrderEvent.CANCEL;
    }
}
