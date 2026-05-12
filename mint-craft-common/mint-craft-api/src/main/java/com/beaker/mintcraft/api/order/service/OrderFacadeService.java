package com.beaker.mintcraft.api.order.service;

import com.beaker.mintcraft.api.order.valobj.TradeOrderVO;
import com.beaker.mintcraft.base.response.SingleResponse;

/**
 * @Author beaker
 * @Date 2026/5/12 20:51
 * @Description 订单 facade 层接口
 */
public interface OrderFacadeService {

    /**
     * 订单详情
     *
     * @param orderId
     * @return
     */
    public SingleResponse<TradeOrderVO> getTradeOrder(String orderId);
}
