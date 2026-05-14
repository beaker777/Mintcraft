package com.beaker.mintcraft.api.order.request.base;

import com.beaker.mintcraft.api.order.constant.TradeOrderEvent;
import com.beaker.mintcraft.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/14 15:54
 * @Description 订单基本请求
 */
@Data
public abstract class BaseOrderRequest extends BaseRequest {

    /**
     * 操作幂等号
     */
    @NotNull(message = "identifier 不能为空")
    private String identifier;

    /**
     * 获取订单事件
     *
     * @return
     */
    public abstract TradeOrderEvent getOrderEvent();
}
