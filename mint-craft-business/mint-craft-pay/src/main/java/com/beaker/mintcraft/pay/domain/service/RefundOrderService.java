package com.beaker.mintcraft.pay.domain.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.api.pay.constant.PayRefundOrderState;
import com.beaker.mintcraft.api.pay.request.RefundCreateRequest;
import com.beaker.mintcraft.base.exception.biz.BizException;
import com.beaker.mintcraft.base.exception.biz.RepoErrorCode;
import com.beaker.mintcraft.pay.domain.entity.PayOrder;
import com.beaker.mintcraft.pay.domain.entity.RefundOrder;
import com.beaker.mintcraft.pay.domain.event.RefundSuccessEvent;
import com.beaker.mintcraft.pay.infrastructure.mapper.PayOrderMapper;
import com.beaker.mintcraft.pay.infrastructure.mapper.RefundOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.beaker.mintcraft.api.pay.constant.PayRefundOrderState.REFUNDING;
import static com.beaker.mintcraft.api.pay.constant.PayRefundOrderState.TO_REFUND;

/**
 * @Author beaker
 * @Date 2026/5/31 15:31
 * @Description 退款单服务
 */
@Service
public class RefundOrderService extends ServiceImpl<RefundOrderMapper, RefundOrder> {

    @Autowired
    private RefundOrderMapper refundOrderMapper;

    @Autowired
    private PayOrderMapper payOrderMapper;

    private static final Logger logger = LoggerFactory.getLogger(PayOrderService.class);

    public RefundOrder create(RefundCreateRequest request) {
        RefundOrder existRefundOrder = refundOrderMapper
                .selectByIdentifier(request.getPayOrderId(), request.getIdentifier(), request.getRefundChannel().toString());

        // 幂等校验
        if (existRefundOrder != null) {
            return existRefundOrder;
        }

        PayOrder payOrder = payOrderMapper.selectByPayOrderId(request.getPayOrderId());

        RefundOrder refundOrder = RefundOrder.create(request, payOrder);
        boolean saveResult = save(refundOrder);
        Assert.isTrue(saveResult, () -> new BizException(RepoErrorCode.INSERT_FAILED));

        return refundOrder;
    }

    public boolean refunding(String refundOrderId) {
        RefundOrder refundOrder = refundOrderMapper.selectByRefundOrderId(refundOrderId);
        refundOrder.refunding();

        boolean saveResult = saveOrUpdate(refundOrder);
        Assert.isTrue(saveResult, () -> new BizException(RepoErrorCode.UPDATE_FAILED));

        return true;
    }

    public boolean refundSuccess(RefundSuccessEvent refundSuccessEvent) {
        RefundOrder refundOrder = refundOrderMapper.selectByRefundOrderId(refundSuccessEvent.getRefundOrderId());
        refundOrder.refundSuccess(refundSuccessEvent);

        boolean saveResult = this.saveOrUpdate(refundOrder);
        Assert.isTrue(saveResult, () -> new BizException(RepoErrorCode.UPDATE_FAILED));

        return true;
    }

    public List<RefundOrder> pageQueryNeedRetryOrders(int pageSize, Long minId) {
        QueryWrapper<RefundOrder> wrapper = new QueryWrapper<>();

        wrapper.in("refund_order_state", REFUNDING, TO_REFUND);
        if (minId != null) {
            wrapper.ge("id", minId);
        }
        wrapper.last("limit " + pageSize);
        wrapper.orderBy(true, true, "gmt_create");

        return this.list(wrapper);
    }

    public RefundOrder queryByOrderId(String refundOrderId) {
        QueryWrapper<RefundOrder> wrapper = new QueryWrapper<>();

        wrapper.eq("refund_order_id", refundOrderId);

        return this.getOne(wrapper);
    }
}
