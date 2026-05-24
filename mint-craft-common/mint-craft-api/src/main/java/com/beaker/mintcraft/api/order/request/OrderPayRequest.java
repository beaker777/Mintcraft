package com.beaker.mintcraft.api.order.request;

import com.beaker.mintcraft.api.order.constant.TradeOrderEvent;
import com.beaker.mintcraft.api.order.request.base.BaseOrderUpdateRequest;
import com.beaker.mintcraft.api.pay.constant.PayChannel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author beaker
 * @Date 2026/5/24 15:42
 * @Description 订单支付请求
 */
@Data
public class OrderPayRequest extends BaseOrderUpdateRequest {

    /**
     * 支付方式
     */
    private PayChannel payChannel;

    /**
     * 支付流水号
     */
    private String payStreamId;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    @Override
    public TradeOrderEvent getOrderEvent() {
        return TradeOrderEvent.PAY;
    }
}
