package com.beaker.mintcraft.api.order.service;

import com.beaker.mintcraft.api.order.request.*;
import com.beaker.mintcraft.api.order.response.OrderResponse;
import com.beaker.mintcraft.api.order.valobj.TradeOrderVO;
import com.beaker.mintcraft.base.response.PageResponse;
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

    /**
     * 订单详情
     *
     * @param orderId
     * @param userId
     * @return
     */
    public SingleResponse<TradeOrderVO> getTradeOrder(String orderId, String userId);

    /**
     * 订单分页查询
     *
     * @param request
     * @return
     */
    public PageResponse<TradeOrderVO> pageQuery(OrderPageQueryRequest request);

    /**
     * 创建订单
     *
     * @param request
     * @return
     */
    public OrderResponse create(OrderCreateRequest request);

    /**
     * 订单确认
     *
     * @param request
     * @return
     */
    public OrderResponse confirm(OrderConfirmRequest request);

    /**
     * 创建并确认订单
     * @param request
     * @return
     */
    public OrderResponse createAndConfirm(OrderCreateAndConfirmRequest request);


    /**
     * 取消订单
     *
     * @param request
     * @return
     */
    public OrderResponse cancel(OrderCancelRequest request);

    /**
     * 订单超时
     *
     * @param request
     * @return
     */
    public OrderResponse timeout(OrderTimeoutRequest request);


    /**
     * 订单支付成功
     *
     * @param request
     * @return
     */
    public OrderResponse paySuccess(OrderPayRequest request);
}
