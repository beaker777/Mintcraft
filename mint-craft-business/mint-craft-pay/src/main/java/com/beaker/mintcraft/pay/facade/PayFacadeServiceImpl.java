package com.beaker.mintcraft.pay.facade;

import cn.hutool.core.lang.Assert;
import com.beaker.mintcraft.api.pay.constant.PayOrderState;
import com.beaker.mintcraft.api.pay.request.PayCreateRequest;
import com.beaker.mintcraft.api.pay.response.PayCreateResponse;
import com.beaker.mintcraft.api.pay.service.PayFacadeService;
import com.beaker.mintcraft.base.exception.biz.BizException;
import com.beaker.mintcraft.base.exception.biz.RepoErrorCode;
import com.beaker.mintcraft.base.utils.MoneyUtils;
import com.beaker.mintcraft.lock.DistributeLock;
import com.beaker.mintcraft.pay.domain.entity.PayOrder;
import com.beaker.mintcraft.pay.domain.service.PayOrderService;
import com.beaker.mintcraft.pay.infrastructure.channel.request.PayChannelRequest;
import com.beaker.mintcraft.pay.infrastructure.channel.response.PayChannelResponse;
import com.beaker.mintcraft.pay.infrastructure.channel.service.PayChannelServiceFactory;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;

import static com.beaker.mintcraft.api.pay.exception.PayErrorCode.ORDER_IS_ALREADY_PAID;

/**
 * @Author beaker
 * @Date 2026/5/23 15:44
 * @Description 支付模块 facade 层实现类
 */
@DubboService
public class PayFacadeServiceImpl implements PayFacadeService {

    @Resource
    private PayOrderService payOrderService;

    @Resource
    private PayChannelServiceFactory payChannelServiceFactory;

    @Override
    @DistributeLock(keyExpression = "#payCreateRequest.bizNo", scene = "GENERATE_PAY_URL")
    public PayCreateResponse generatePayUrl(PayCreateRequest payCreateRequest) {
        PayCreateResponse response = new PayCreateResponse();

        // 创建支付单
        PayOrder payOrder = payOrderService.create(payCreateRequest);

        // 若支付单状态为 paying 说明已经生成过二维码了
        if (payOrder.getOrderState() == PayOrderState.PAYING) {
            response.setPayOrderId(payOrder.getPayOrderId());
            response.setPayUrl(payOrder.getPayUrl());
            response.setSuccess(true);

            return response;
        }

        // 订单已经支付, 直接返回
        if (payOrder.isPaid()) {
            response.setSuccess(false);
            response.setResponseCode(ORDER_IS_ALREADY_PAID.getCode());
            response.setResponseMessage(ORDER_IS_ALREADY_PAID.getMessage());

            return response;
        }

        // 获取到支付 url
        PayChannelResponse payChannelResponse = doPay(payCreateRequest, payOrder);

        if (payChannelResponse.getSuccess()) {
            // 更新订单状态
            Boolean updateResult = payOrderService.paying(payOrder.getPayOrderId(), payChannelResponse.getPayUrl());
            Assert.isTrue(updateResult, () -> new BizException(RepoErrorCode.UPDATE_FAILED));

            response.setSuccess(true);
            response.setPayOrderId(payOrder.getPayOrderId());
            response.setPayUrl(payChannelResponse.getPayUrl());
        } else {
            response.setSuccess(false);
            response.setResponseCode(payChannelResponse.getResponseCode());
            response.setResponseMessage(payChannelResponse.getResponseMessage());
        }

        return response;
    }

    private PayChannelResponse doPay(PayCreateRequest payCreateRequest, PayOrder payOrder) {
        PayChannelRequest payChannelRequest = new PayChannelRequest();
        payChannelRequest.setAmount(MoneyUtils.yuanToCent(payCreateRequest.getOrderAmount()));
        payChannelRequest.setDescription(payCreateRequest.getMemo());
        payChannelRequest.setOrderId(payOrder.getPayOrderId());
        payChannelRequest.setAttach(payCreateRequest.getBizNo());
        payChannelRequest.setExpireTime(DateUtils.addMinutes(payOrder.getGmtCreate(), PayOrder.DEFAULT_TIME_OUT_MINUTES));

        return payChannelServiceFactory.get(payCreateRequest.getPayChannel()).pay(payChannelRequest);
    }
}
