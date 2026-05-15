package com.beaker.mintcraft.api.order.request;

import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.order.constant.TradeOrderEvent;
import com.beaker.mintcraft.api.order.request.base.BaseOrderUpdateRequest;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/15 20:00
 * @Description 订单确认请求
 */
@Data
public class OrderConfirmRequest extends BaseOrderUpdateRequest {

    /**
     * 买家Id
     */
    private String buyerId;

    /**
     * 商品Id
     */
    private String goodsId;

    /**
     * 商品类型
     */
    private GoodsType goodsType;

    /**
     * 数量
     */
    private Integer itemCount;

    @Override
    public TradeOrderEvent getOrderEvent() {
        return TradeOrderEvent.CONFIRM;
    }
}
