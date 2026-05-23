package com.beaker.mintcraft.pay.infrastructure.channel.service.impl;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.beaker.mintcraft.api.pay.constant.PayChannel;
import com.beaker.mintcraft.base.utils.MoneyUtils;
import com.beaker.mintcraft.pay.domain.event.PaySuccessEvent;
import com.beaker.mintcraft.pay.infrastructure.channel.request.PayChannelRequest;
import com.beaker.mintcraft.pay.infrastructure.channel.response.PayChannelResponse;
import com.beaker.mintcraft.pay.infrastructure.channel.service.PayChannelService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

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
public class MockPayChannelService implements PayChannelService {

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

            // TODO 调用 paySuccess
        } catch (Exception e) {
            log.error("notify error", e);
            return false;
        }

        return true;
    }
}
