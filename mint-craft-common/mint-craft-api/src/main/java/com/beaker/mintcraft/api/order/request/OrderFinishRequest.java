package com.beaker.mintcraft.api.order.request;

import com.beaker.mintcraft.api.order.constant.TradeOrderEvent;
import com.beaker.mintcraft.api.order.request.base.BaseOrderUpdateRequest;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/26 19:24
 * @Description 订单完成请求
 */
@Data
public class OrderFinishRequest extends BaseOrderUpdateRequest {

    @Override
    public TradeOrderEvent getOrderEvent() {
        return TradeOrderEvent.FINISH;
    }
}
