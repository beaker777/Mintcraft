package com.beaker.mintcraft.api.pay.service;

import com.beaker.mintcraft.api.pay.request.PayCreateRequest;
import com.beaker.mintcraft.api.pay.request.PayQueryRequest;
import com.beaker.mintcraft.api.pay.response.PayCreateResponse;
import com.beaker.mintcraft.api.pay.valobj.PayOrderVO;
import com.beaker.mintcraft.base.response.MultiResponse;
import com.beaker.mintcraft.base.response.SingleResponse;

/**
 * @Author beaker
 * @Date 2026/5/23 15:43
 * @Description 支付模块 facade 层接口
 */
public interface PayFacadeService {


    /**
     * 生成支付链接
     *
     * @param payCreateRequest
     * @return
     */
    public PayCreateResponse generatePayUrl(PayCreateRequest payCreateRequest);

    /**
     * 查询支付订单
     *
     * @param payQueryRequest
     * @return
     */
    public MultiResponse<PayOrderVO> queryPayOrders(PayQueryRequest payQueryRequest);

    /**
     * 查询支付订单
     *
     * @param payOrderId
     * @return
     */
    public SingleResponse<PayOrderVO> queryPayOrder(String payOrderId);

    /**
     * 查询支付订单
     *
     * @param payOrderId
     * @param payerId
     * @return
     */
    public SingleResponse<PayOrderVO> queryPayOrder(String payOrderId, String payerId);
}
