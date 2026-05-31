package com.beaker.mintcraft.order.job;

import com.beaker.mintcraft.api.common.constant.BizOrderType;
import com.beaker.mintcraft.api.order.request.OrderConfirmRequest;
import com.beaker.mintcraft.api.order.request.OrderTimeoutRequest;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.pay.constant.PayOrderState;
import com.beaker.mintcraft.api.pay.request.PayQueryRequest;
import com.beaker.mintcraft.api.pay.request.condition.PayQueryByBizNo;
import com.beaker.mintcraft.api.pay.service.PayFacadeService;
import com.beaker.mintcraft.api.pay.valobj.PayOrderVO;
import com.beaker.mintcraft.api.user.constant.UserType;
import com.beaker.mintcraft.base.response.MultiResponse;
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import com.beaker.mintcraft.order.domain.service.OrderManageService;
import com.beaker.mintcraft.order.domain.service.OrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
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

    private final BlockingQueue<TradeOrder> orderConfirmBlockingQueue = new LinkedBlockingQueue<>(CAPACITY);

    private final ForkJoinPool forkJoinPool = new ForkJoinPool(10);

    @Autowired
    private OrderService orderService;

    @Resource
    private OrderFacadeService orderFacadeService;

    @Resource
    private PayFacadeService payFacadeService;

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

    @XxlJob("orderConfirmExecute")
    public ReturnT<String> orderConfirmExecute() {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        LOG.info("orderConfirmExecute start to execute , shardIndex is {} , shardTotal is {}", shardIndex, shardTotal);

        List<String> buyerIdTailNumberList = new ArrayList<>();
        for (int i = 0; i <= MAX_TAIL_NUMBER; i++) {
            if (i % shardTotal == shardIndex) {
                buyerIdTailNumberList.add(StringUtils.leftPad(String.valueOf(i), 2, "0"));
            }
        }

        buyerIdTailNumberList.forEach(buyerIdTailNumber -> {
            try {
                List<TradeOrder> tradeOrders = orderService.pageQueryNeedConfirmOrders(PAGE_SIZE, buyerIdTailNumber, null);
                orderConfirmBlockingQueue.addAll(tradeOrders);

                forkJoinPool.execute(this::executeConfirm);

                while (CollectionUtils.isNotEmpty(tradeOrders)) {
                    long maxId = tradeOrders.stream().mapToLong(TradeOrder::getId).max().orElse(Long.MAX_VALUE);

                    tradeOrders = orderService.pageQueryNeedConfirmOrders(PAGE_SIZE, buyerIdTailNumber, maxId + 1);
                    orderConfirmBlockingQueue.addAll(tradeOrders);
                }
            } finally {
                orderConfirmBlockingQueue.add(POISON);
                LOG.debug("POISON added to blocking queue ，buyerIdTailNumber is {}", buyerIdTailNumber);
            }
        });

        return ReturnT.SUCCESS;
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

    private void executeConfirm() {
        TradeOrder tradeOrder = null;
        try {
            while (true) {
                tradeOrder = orderConfirmBlockingQueue.take();

                // 获取到毒丸对象, 停止本次扫描
                if (tradeOrder == POISON) {
                    LOG.debug("POISON toked from blocking queue");
                    break;
                }

                executeConfirmSingle(tradeOrder);
            }
        } catch (InterruptedException e) {
            LOG.error("executeConfirm failed", e);
        }
        LOG.debug("executeConfirm finish");
    }


    private void executeTimeoutSingle(TradeOrder tradeOrder) {
        // 查询订单是否已经支付成功
        PayQueryRequest payQueryRequest = new PayQueryRequest();
        payQueryRequest.setPayerId(tradeOrder.getBuyerId());
        payQueryRequest.setPayOrderState(PayOrderState.PAID);

        PayQueryByBizNo payQueryByBizNo = new PayQueryByBizNo();
        payQueryByBizNo.setBizNo(tradeOrder.getOrderId());
        payQueryByBizNo.setBizType(BizOrderType.TRADE_ORDER.name());
        payQueryRequest.setPayQueryCondition(payQueryByBizNo);

        MultiResponse<PayOrderVO> payQueryResponse = payFacadeService.queryPayOrders(payQueryRequest);

        if (payQueryResponse.getSuccess() && CollectionUtils.isEmpty(payQueryResponse.getDatas())) {
            LOG.info("start to execute order timeout , orderId is {}", tradeOrder.getOrderId());

            OrderTimeoutRequest orderTimeoutRequest = new OrderTimeoutRequest();
            orderTimeoutRequest.setOrderId(tradeOrder.getOrderId());
            orderTimeoutRequest.setOperateTime(new Date());
            orderTimeoutRequest.setOperator(UserType.PLATFORM.name());
            orderTimeoutRequest.setOperatorType(UserType.PLATFORM);
            orderTimeoutRequest.setIdentifier(tradeOrder.getOrderId());

            orderFacadeService.timeout(orderTimeoutRequest);
        }
    }

    private void executeConfirmSingle(TradeOrder tradeOrder) {
        OrderConfirmRequest confirmRequest = new OrderConfirmRequest();

        confirmRequest.setOperator(UserType.PLATFORM.name());
        confirmRequest.setOperatorType(UserType.PLATFORM);
        confirmRequest.setOrderId(tradeOrder.getOrderId());
        confirmRequest.setIdentifier(tradeOrder.getIdentifier());
        confirmRequest.setOperateTime(new Date());
        confirmRequest.setOrderId(tradeOrder.getOrderId());
        confirmRequest.setBuyerId(tradeOrder.getBuyerId());
        confirmRequest.setItemCount(tradeOrder.getItemCount());
        confirmRequest.setGoodsId(tradeOrder.getGoodsId());
        confirmRequest.setGoodsType(tradeOrder.getGoodsType());

        orderFacadeService.confirm(confirmRequest);
    }
}
