package com.beaker.mintcraft.order.job;

import com.beaker.mintcraft.api.order.request.OrderTimeoutRequest;
import com.beaker.mintcraft.api.user.constant.UserType;
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import com.beaker.mintcraft.order.domain.service.OrderManageService;
import com.beaker.mintcraft.order.domain.service.OrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author beaker
 * @Date 2026/5/22 19:08
 * @Description 订单定时任务
 */
@Component
public class OrderJob {

    private static final Logger LOG = LoggerFactory.getLogger(OrderJob.class);

    private static final int MAX_TAIL_NUMBER = 99;

    private  static final int PAGE_SIZE = 500;

    private static final int CAPACITY = 2000;

    private static final TradeOrder POISON = new TradeOrder();

    private final BlockingQueue<TradeOrder> orderTimeoutBlockingQueue = new LinkedBlockingQueue<>(CAPACITY);

    private final ForkJoinPool forkJoinPool = new ForkJoinPool(10);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderManageService orderManageService;

    @XxlJob("orderTimeOutExecute")
    public ReturnT<String> orderTimeOutExecute() {
        try {
            int shardIndex = XxlJobHelper.getShardIndex();
            int shardTotal = XxlJobHelper.getShardTotal();

            LOG.info("orderTimeOutExecute start to execute , shardIndex is {} , shardTotal is {}", shardIndex, shardTotal);

            // 如果用户 id 的尾号取模后与本机 index 相等, 由本机负责扫描
            List<String> buyerIdTailNumberList = new ArrayList<>();
            for (int i = 0; i <= MAX_TAIL_NUMBER; i++) {
                if (i % shardTotal == shardIndex) {
                    buyerIdTailNumberList.add(StringUtils.leftPad(String.valueOf(i), 2, "0"));
                }
            }

            // 根据每个尾号, 扫描对应的订单
            buyerIdTailNumberList.forEach(buyerIdTailNumber -> {
                try {
                    List<TradeOrder> tradeOrders = orderService.pageQueryTimeoutOrders(PAGE_SIZE, buyerIdTailNumber, null);
                    orderTimeoutBlockingQueue.addAll(tradeOrders);

                    forkJoinPool.execute(this::executeTimeout);

                    // 边执行关单, 边继续向 queue 中添加 order
                    while (CollectionUtils.isNotEmpty(tradeOrders)) {
                        long maxId = tradeOrders.stream().mapToLong(TradeOrder::getId).max().orElse(Long.MAX_VALUE);

                        tradeOrders = orderService.pageQueryTimeoutOrders(PAGE_SIZE, buyerIdTailNumber, maxId + 1);
                        orderTimeoutBlockingQueue.addAll(tradeOrders);
                    }
                } finally {
                    // 等待添加完所有订单后, 添加毒丸对象
                    orderTimeoutBlockingQueue.add(POISON);
                    LOG.debug("POISON added to blocking queue , buyerIdTailNumber is {}", buyerIdTailNumber);
                }
            });

            return ReturnT.SUCCESS;
        } catch (Exception e) {
            LOG.error("orderTimeOutExecute failed", e);
            throw e;
        }
    }

    private void executeTimeout() {
        TradeOrder tradeOrder = null;
        try {
            while (true) {
                tradeOrder = orderTimeoutBlockingQueue.take();

                // 如果获取到毒丸对象, 退出
                if (tradeOrder == POISON) {
                    LOG.debug("POISON is taken from blocking queue");
                    break;
                }

                // 执行订单取消
                executeTimeoutSingle(tradeOrder);
                LOG.info("executeTimeout tradeOrderId:{}", tradeOrder.getOrderId());
            }
        } catch (InterruptedException e) {
            LOG.error("executeTimeout failed", e);
        }

        LOG.debug("executeTimeout finish");
    }

    private void executeTimeoutSingle(TradeOrder tradeOrder) {
        // TODO: 这里后续要对接支付模块

        LOG.info("start to execute order timeout , orderId is {}", tradeOrder.getOrderId());

        OrderTimeoutRequest orderTimeoutRequest = new OrderTimeoutRequest();
        orderTimeoutRequest.setOrderId(tradeOrder.getOrderId());
        orderTimeoutRequest.setOperateTime(new Date());
        orderTimeoutRequest.setOperator(UserType.PLATFORM.name());
        orderTimeoutRequest.setOperatorType(UserType.PLATFORM);
        orderTimeoutRequest.setIdentifier(tradeOrder.getOrderId());

        orderManageService.timeout(orderTimeoutRequest);
    }
}
