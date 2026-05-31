package com.beaker.mintcraft.pay.job;

import com.beaker.mintcraft.api.pay.constant.PayRefundOrderState;
import com.beaker.mintcraft.base.utils.MoneyUtils;
import com.beaker.mintcraft.pay.domain.entity.RefundOrder;
import com.beaker.mintcraft.pay.domain.service.RefundOrderService;
import com.beaker.mintcraft.pay.infrastructure.channel.request.RefundChannelRequest;
import com.beaker.mintcraft.pay.infrastructure.channel.response.RefundChannelResponse;
import com.beaker.mintcraft.pay.infrastructure.channel.service.PayChannelServiceFactory;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/5/31 20:19
 * @Description 退款重试任务
 */
@Component
public class RefundOrderRetryJob {

    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    @Lazy
    private PayChannelServiceFactory payChannelServiceFactory;

    private static final int PAGE_SIZE = 100;

    private static final Logger LOG = LoggerFactory.getLogger(RefundOrderRetryJob.class);

    @XxlJob("refundOrderRetryJob")
    public ReturnT<String> execute() {
        List<RefundOrder> refundOrders = refundOrderService.pageQueryNeedRetryOrders(PAGE_SIZE, null);

        while(CollectionUtils.isNotEmpty(refundOrders)) {
            refundOrders.forEach(this::executeSingle);

            long maxId = refundOrders.stream().mapToLong(RefundOrder::getId).max().orElse(Integer.MAX_VALUE);
            refundOrders = refundOrderService.pageQueryNeedRetryOrders(PAGE_SIZE, maxId + 1);
        }

        return ReturnT.SUCCESS;
    }

    private void executeSingle(RefundOrder refundOrder) {
        LOG.info("start to execute refund , orderId is {}", refundOrder.getPayOrderId());

        RefundChannelRequest refundChannelRequest = new RefundChannelRequest();
        refundChannelRequest.setRefundOrderId(refundOrder.getRefundOrderId());
        refundChannelRequest.setPaidAmount(MoneyUtils.yuanToCent(refundOrder.getPaidAmount()));
        refundChannelRequest.setPayChannelStreamId(refundOrder.getPayChannelStreamId());
        refundChannelRequest.setPayOrderId(refundOrder.getPayOrderId());
        refundChannelRequest.setRefundAmount(MoneyUtils.yuanToCent(refundOrder.getApplyRefundAmount()));
        refundChannelRequest.setRefundReason(refundOrder.getMemo());

        // 进行异步退款
        RefundChannelResponse response = payChannelServiceFactory.get(refundOrder.getRefundChannel()).refund(refundChannelRequest);

        if (refundOrder.getRefundOrderState() == PayRefundOrderState.TO_REFUND && response.getSuccess()) {
            refundOrderService.refunding(refundOrder.getRefundOrderId());
        }
    }
}
