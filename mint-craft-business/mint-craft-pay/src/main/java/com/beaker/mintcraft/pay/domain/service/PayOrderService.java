package com.beaker.mintcraft.pay.domain.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.api.pay.constant.PayOrderState;
import com.beaker.mintcraft.api.pay.request.PayCreateRequest;
import com.beaker.mintcraft.api.pay.response.PayCreateResponse;
import com.beaker.mintcraft.base.exception.biz.BizException;
import com.beaker.mintcraft.base.exception.biz.RepoErrorCode;
import com.beaker.mintcraft.pay.domain.entity.PayOrder;
import com.beaker.mintcraft.pay.domain.event.PaySuccessEvent;
import com.beaker.mintcraft.pay.domain.event.RefundSuccessEvent;
import com.beaker.mintcraft.pay.infrastructure.mapper.PayOrderMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author beaker
 * @Date 2026/5/23 15:47
 * @Description 支付单服务
 */
@Service
public class PayOrderService extends ServiceImpl<PayOrderMapper, PayOrder> {

    private static final Logger logger = LoggerFactory.getLogger(PayOrderService.class);

    @Autowired
    private PayOrderMapper payOrderMapper;

    public PayOrder create(PayCreateRequest payCreateRequest) {
        PayOrder existPayOrder = payOrderMapper
                .selectByBizNoAndPayer(payCreateRequest.getPayerId(), payCreateRequest.getBizNo(), payCreateRequest.getBizType().name(), payCreateRequest.getPayChannel().name());

        // 如果支付单已经创建过了, 且还没有过期, 直接返回
        if (existPayOrder != null && existPayOrder.getOrderState() != PayOrderState.EXPIRED) {
            return existPayOrder;
        }

        // 创建支付单, 插入数据库
        PayOrder payOrder = PayOrder.create(payCreateRequest);
        boolean saveResult = save(payOrder);
        Assert.isTrue(saveResult, () -> new BizException(RepoErrorCode.INSERT_FAILED));

        return payOrder;
    }

    public Boolean paying(String payOrderId, String payUrl) {
        PayOrder payOrder = payOrderMapper.selectByPayOrderId(payOrderId);
        payOrder.paying(payUrl);

        boolean saveResult = saveOrUpdate(payOrder);
        Assert.isTrue(saveResult, () -> new BizException(RepoErrorCode.UPDATE_FAILED));

        return true;
    }

    public Boolean paySuccess(PaySuccessEvent paySuccessEvent) {
        PayOrder payOrder = payOrderMapper.selectByPayOrderId(paySuccessEvent.getPayOrderId());
        payOrder.paySuccess(paySuccessEvent);

        boolean saveResult = saveOrUpdate(payOrder);
        Assert.isTrue(saveResult, () -> new BizException(RepoErrorCode.UPDATE_FAILED));

        return true;
    }

    public Boolean payExpired(String payOrderId) {
        PayOrder payOrder = payOrderMapper.selectByPayOrderId(payOrderId);
        payOrder.payExpired();

        boolean updateResult = saveOrUpdate(payOrder);
        Assert.isTrue(updateResult, () -> new BizException(RepoErrorCode.UPDATE_FAILED));

        return true;
    }

    public Boolean refundSuccess(RefundSuccessEvent refundSuccessEvent) {
        PayOrder payOrder = payOrderMapper.selectByPayOrderId(refundSuccessEvent.getPayOrderId());
        payOrder.refundSuccess(refundSuccessEvent);

        boolean saveResult = saveOrUpdate(payOrder);
        Assert.isTrue(saveResult, () -> new BizException(RepoErrorCode.UPDATE_FAILED));

        return true;
    }

    public List<PayOrder> queryByBizNo(String bizNo, String bizOrderType, String payerId, PayOrderState payOrderState) {
        QueryWrapper<PayOrder> wrapper = new QueryWrapper<>();

        wrapper.eq("biz_no", bizNo);
        wrapper.eq("biz_type", bizOrderType);
        wrapper.eq("pay_id", payerId);
        wrapper.eq("state", payOrderState.name());

        return this.list(wrapper);
    }

    public PayOrder queryByOrderId(String payOrderId) {
        QueryWrapper<PayOrder> wrapper = new QueryWrapper<>();

        wrapper.eq("pay_order_id", payOrderId);

        return this.getOne(wrapper);
    }

    public PayOrder queryByOrderIdAndPayer(String payOrderId, String payerId) {
        QueryWrapper<PayOrder> wrapper = new QueryWrapper<>();

        wrapper.eq("pay_order_id", payOrderId);
        wrapper.eq("payer_id", payerId);

        return this.getOne(wrapper);
    }

    public List<PayOrder> pageQueryTimeoutOrders(int pageSize, Long minId) {
        QueryWrapper<PayOrder> wrapper = new QueryWrapper<>();

        wrapper.eq("order_state", PayOrderState.PAYING);
        wrapper.lt("gmt_create", DateUtils.addMinutes(new Date(), -PayOrder.DEFAULT_TIME_OUT_MINUTES));
        if (minId != null) {
            wrapper.ge("id", minId);
        }
        wrapper.orderBy(true, true, "gmt_create");
        wrapper.last("limit " + pageSize);

        return this.list(wrapper);
    }
}
