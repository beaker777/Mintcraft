package com.beaker.mintcraft.pay.infrastructure.channel.service;

import com.beaker.mintcraft.pay.infrastructure.channel.request.PayChannelRequest;
import com.beaker.mintcraft.pay.infrastructure.channel.request.RefundChannelRequest;
import com.beaker.mintcraft.pay.infrastructure.channel.response.PayChannelResponse;
import com.beaker.mintcraft.pay.infrastructure.channel.response.RefundChannelResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @Author beaker
 * @Date 2026/5/23 16:31
 * @Description 支付渠道服务
 */
public interface PayChannelService {

    /**
     * 支付
     *
     * @param payChannelRequest
     * @return
     */
    PayChannelResponse pay(PayChannelRequest payChannelRequest);

    /**
     * 支付结果回调
     *
     * @param request
     * @param response
     * @return 通知结果
     */
    boolean notify(HttpServletRequest request, HttpServletResponse response);

    /**
     * 退款
     *
     * @param refundChannelRequest
     * @return
     */
    RefundChannelResponse refund(RefundChannelRequest refundChannelRequest);

    /**
     * 退款结果回调
     *
     * @param request
     * @param response
     * @return
     */
    boolean refundNotify(HttpServletRequest request, HttpServletResponse response);
}
