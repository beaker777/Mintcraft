package com.beaker.mintcraft.pay.job;

import com.beaker.mintcraft.pay.domain.entity.PayOrder;
import com.beaker.mintcraft.pay.domain.service.PayOrderService;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/6/2 21:08
 * @Description 支付单超时任务
 */
@Component
public class PayOrderTimeoutJob {

    @Autowired
    private PayOrderService payOrderService;

    private static final int PAGE_SIZE = 100;

    private static final Logger LOG = LoggerFactory.getLogger(PayOrderTimeoutJob.class);

    public ReturnT<String> execute() {
        List<PayOrder> payOrders = payOrderService.pageQueryTimeoutOrders(PAGE_SIZE, null);

        while (CollectionUtils.isNotEmpty(payOrders)) {
            payOrders.forEach(this::executeSingle);

            Long maxId = payOrders.stream().mapToLong(PayOrder::getId).max().orElse(Integer.MAX_VALUE);
            payOrders = payOrderService.pageQueryTimeoutOrders(PAGE_SIZE, maxId + 1);
        }

        return ReturnT.SUCCESS;
    }

    private void executeSingle(PayOrder payOrder) {
        LOG.info("start to execute order timeout , orderId is {}", payOrder.getPayOrderId());

        // fixme: 这里可能出现用户刚支付完, 还没有更新订单状态就被扫描到了, 可能引起客诉
        payOrderService.payExpired(payOrder.getPayOrderId());
    }
}
