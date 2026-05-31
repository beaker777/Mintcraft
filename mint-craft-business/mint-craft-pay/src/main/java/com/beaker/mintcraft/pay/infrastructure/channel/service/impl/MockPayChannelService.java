package com.beaker.mintcraft.pay.infrastructure.channel.service.impl;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.beaker.mintcraft.api.pay.constant.PayChannel;
import com.beaker.mintcraft.base.utils.MoneyUtils;
import com.beaker.mintcraft.pay.application.PayApplicationService;
import com.beaker.mintcraft.pay.domain.event.PaySuccessEvent;
import com.beaker.mintcraft.pay.domain.event.RefundSuccessEvent;
import com.beaker.mintcraft.pay.infrastructure.channel.request.PayChannelRequest;
import com.beaker.mintcraft.pay.infrastructure.channel.request.RefundChannelRequest;
import com.beaker.mintcraft.pay.infrastructure.channel.response.PayChannelResponse;
import com.beaker.mintcraft.pay.infrastructure.channel.response.RefundChannelResponse;
import com.beaker.mintcraft.pay.infrastructure.channel.service.PayChannelService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @Author beaker
 * @Date 2026/5/23 16:42
 * @Description mock 支付渠道
 */
@Slf4j
@Service
public class MockPayChannelService implements PayChannelService {

    @Autowired
    private PayApplicationService payApplicationService;

    public static TransmittableThreadLocal<Map> context = new TransmittableThreadLocal<>();

    private static ThreadFactory chainResultProcessFactory = new ThreadFactoryBuilder()
            .setNameFormat("pay-process-pool-%d").build();

    ScheduledExecutorService scheduler = TtlExecutors.getTtlScheduledExecutorService(new ScheduledThreadPoolExecutor(10, chainResultProcessFactory));


    @Override
    public PayChannelResponse pay(PayChannelRequest payChannelRequest) {
        PayChannelResponse payChannelResponse = new PayChannelResponse();
        payChannelResponse.setSuccess(true);
        payChannelResponse.setPayUrl("https://www.mintcraft.com");

        Map<String, Serializable> params = new HashMap<>();
        params.put("payOrderId", payChannelRequest.getOrderId());
        params.put("paidAmount", payChannelRequest.getAmount());
        context.set(params);

        // 异步线程延迟 3s 后调用 notify 方法
        scheduler.schedule(() -> {
            this.notify(null, null);
        }, 3, TimeUnit.SECONDS);

        return payChannelResponse;
    }

    @Override
    public boolean notify(HttpServletRequest request, HttpServletResponse response) {
        try {
            PaySuccessEvent paySuccessEvent = new PaySuccessEvent();
            paySuccessEvent.setChannelStreamId(UUID.randomUUID().toString());

            Map<String, Serializable> params = (Map<String, Serializable>) context.get();
            paySuccessEvent.setPaidAmount(MoneyUtils.centToYuan((Long) params.get("paidAmount")));
            paySuccessEvent.setPayOrderId((String) params.get("payOrderId"));
            paySuccessEvent.setPaySucceedTime(new Date());
            paySuccessEvent.setPayChannel(PayChannel.MOCK);

            payApplicationService.paySuccess(paySuccessEvent);
        } catch (Exception e) {
            log.error("notify error", e);
            return false;
        }

        return true;
    }

    @Override
    public RefundChannelResponse refund(RefundChannelRequest refundChannelRequest) {
        RefundChannelResponse refundChannelResponse = new RefundChannelResponse();
        refundChannelResponse.setSuccess(true);

        Map<String, Serializable> params = new HashMap<>();
        params.put("payOrderId", refundChannelRequest.getPayOrderId());
        params.put("refundOrderId", refundChannelRequest.getRefundOrderId());
        params.put("refundAmount", refundChannelRequest.getRefundAmount());
        context.set(params);

        // 异步线程延迟 3s 后调用 notify 方法.
        scheduler.schedule(() -> {
            this.refundNotify(null, null);
        },3, TimeUnit.SECONDS);

        return refundChannelResponse;
    }

    @Override
    public boolean refundNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
            RefundSuccessEvent refundSuccessEvent = new RefundSuccessEvent();
            refundSuccessEvent.setChannelStreamId(UUID.randomUUID().toString());

            Map<String, Serializable> params = (Map<String, Serializable>) context.get();
            refundSuccessEvent.setRefundOrderId((String) params.get("refundOrderId"));
            refundSuccessEvent.setPayOrderId((String) params.get("payOrderId"));
            refundSuccessEvent.setRefundedAmount(MoneyUtils.centToYuan((Long) params.get("refundAmount")));
            refundSuccessEvent.setRefundedTime(new Date());
            refundSuccessEvent.setRefundChannel(PayChannel.MOCK);

            payApplicationService.refundSuccess(refundSuccessEvent);
        } catch (Exception e) {
            log.error("notify error", e);
            return false;
        }

        return true;
    }
}
