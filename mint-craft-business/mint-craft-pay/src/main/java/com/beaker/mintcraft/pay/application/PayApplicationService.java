package com.beaker.mintcraft.pay.application;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson2.JSON;
import com.beaker.mintcraft.api.collection.constant.GoodsSaleBizType;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.goods.request.GoodsSaleRequest;
import com.beaker.mintcraft.api.goods.response.GoodsSaleResponse;
import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.order.request.OrderPayRequest;
import com.beaker.mintcraft.api.order.response.OrderResponse;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.order.valobj.TradeOrderVO;
import com.beaker.mintcraft.api.pay.exception.PayErrorCode;
import com.beaker.mintcraft.api.pay.request.RefundCreateRequest;
import com.beaker.mintcraft.base.exception.biz.BizException;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.base.utils.MoneyUtils;
import com.beaker.mintcraft.pay.domain.entity.PayOrder;
import com.beaker.mintcraft.pay.domain.entity.RefundOrder;
import com.beaker.mintcraft.pay.domain.event.PaySuccessEvent;
import com.beaker.mintcraft.pay.domain.event.RefundSuccessEvent;
import com.beaker.mintcraft.pay.domain.service.PayOrderService;
import com.beaker.mintcraft.pay.domain.service.RefundOrderService;
import com.beaker.mintcraft.pay.infrastructure.channel.request.RefundChannelRequest;
import com.beaker.mintcraft.pay.infrastructure.channel.response.RefundChannelResponse;
import com.beaker.mintcraft.pay.infrastructure.channel.service.PayChannelServiceFactory;
import com.beaker.mintcraft.rpc.support.RemoteCallWrapper;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.transaction.TransactionHookManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.ORDER_ALREADY_CLOSED;
import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.ORDER_ALREADY_PAID;

/**
 * @Author beaker
 * @Date 2026/5/24 15:29
 * @Description pay 模块回调入口
 */
@Service
@Slf4j
public class PayApplicationService {

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    @Lazy
    private PayChannelServiceFactory payChannelServiceFactory;

    @Autowired
    private OrderFacadeService orderFacadeService;

    @Autowired
    private GoodsFacadeService goodsFacadeService;

    private static final String REFUND_MEMO_PREFIX = "退款:";

    /**
     * 支付成功
     * <pre>
     *     正常支付成功：
     *     1、查询订单状态
     *     2、推进订单状态到支付成功
     *     3、创建持有的藏品
     *     4、持有的藏品上链
     *     5、推进支付状态到支付成功
     *
     *      重复支付：
     *      1、查询订单状态
     *      2、尝试推进订单状态到支付成功失败, 进入退款流程
     *      2、创建退款单
     *      3、重试退款直到成功
     * </pre>
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean paySuccess(PaySuccessEvent paySuccessEvent) {
        PayOrder payOrder = payOrderService.queryByOrderId(paySuccessEvent.getPayOrderId());

        // 如果已经完成支付了, 直接返回
        if (payOrder.isPaid()) {
            return true;
        }

        // 查询支付单对应的订单
        SingleResponse<TradeOrderVO> response = orderFacadeService.getTradeOrder(payOrder.getBizNo());
        TradeOrderVO tradeOrderVO = response.getData();

        // 将订单状态更新为 PAID
        OrderPayRequest orderPayRequest = getOrderPayRequest(paySuccessEvent, payOrder);
        OrderResponse orderResponse = RemoteCallWrapper
                .call(req -> orderFacadeService.paySuccess(req), orderPayRequest, "orderFacadeService.pay", false);

        // 如果订单已经被其他渠道的支付推进到支付成功, 或者已经关闭订单了, 启动退款流程
        if (needChargeBack(orderResponse)) {
            log.info("order already paid, do chargeback," + payOrder.getBizNo());

            // 先推进支付单状态
            Boolean result = payOrderService.paySuccess(paySuccessEvent);
            Assert.isTrue(result, () -> new BizException(PayErrorCode.PAY_SUCCESS_NOTICE_FAILED));

            // 异步退款
            doChargeBack(paySuccessEvent, tradeOrderVO);

            return true;
        }

        // 订单状态更新失败
        if (!orderResponse.getSuccess()) {
            log.error("orderFacadeService.pay error, response = {}", JSON.toJSONString(orderResponse));
            return false;
        }

        // 创建持有藏品 -> MySQL
        GoodsSaleRequest goodsSaleRequest = getGoodsSaleRequest(tradeOrderVO);
        GoodsSaleResponse goodsSaleResponse = RemoteCallWrapper
                .call(req -> goodsFacadeService.paySuccess(req), goodsSaleRequest, "goodsFacadeService.confirmSale");

        // 如果商品类型是 COLLECTION 进行上链
        if (tradeOrderVO.getGoodsType() == GoodsType.COLLECTION) {
            TransactionHookManager.registerHook(new PaySuccessTransactionHook(goodsSaleResponse.getHeldCollectionId()));
        }

        // 更新支付单状态
        Boolean result = payOrderService.paySuccess(paySuccessEvent);
        Assert.isTrue(result, () -> new BizException(PayErrorCode.PAY_SUCCESS_NOTICE_FAILED));

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean refundSuccess(RefundSuccessEvent refundSuccessEvent) {
        RefundOrder refundOrder = refundOrderService.queryByOrderId(refundSuccessEvent.getRefundOrderId());

        // 幂等校验
        if (refundOrder.isRefunded()) {
            return true;
        }

        // 更新支付单和退款单状态
        boolean refundResult = payOrderService.refundSuccess(refundSuccessEvent) && refundOrderService.refundSuccess(refundSuccessEvent);
        Assert.isTrue(refundResult, () -> new BizException(PayErrorCode.REFUND_SUCCESS_NOTICE_FAILED));

        return true;
    }

    private boolean needChargeBack(OrderResponse orderResponse) {
        return orderResponse.getResponseCode() != null
                && (orderResponse.getResponseCode().equals(ORDER_ALREADY_PAID.getCode())
                || orderResponse.getResponseCode().equals(ORDER_ALREADY_CLOSED.getCode()));
    }

    private void doChargeBack(PaySuccessEvent paySuccessEvent, TradeOrderVO tradeOrderVO) {
        RefundCreateRequest refundCreateRequest = new RefundCreateRequest();
        refundCreateRequest.setIdentifier(paySuccessEvent.getChannelStreamId());
        refundCreateRequest.setMemo(REFUND_MEMO_PREFIX + tradeOrderVO.getOrderId());
        refundCreateRequest.setPayOrderId(paySuccessEvent.getPayOrderId());
        refundCreateRequest.setRefundAmount(paySuccessEvent.getPaidAmount());
        refundCreateRequest.setRefundChannel(paySuccessEvent.getPayChannel());

        // 创建退款单, 状态为 TO_REFUND
        RefundOrder refundOrder = refundOrderService.create(refundCreateRequest);
        Assert.notNull(refundOrder, () -> new BizException(PayErrorCode.REFUND_CREATE_FAILED));

        // 异步退款, 失败后交给定时任务重试
        Thread.ofVirtual().start(() -> {
            RefundChannelRequest refundChannelRequest = new RefundChannelRequest();
            refundChannelRequest.setRefundOrderId(refundOrder.getRefundOrderId());
            refundChannelRequest.setPaidAmount(MoneyUtils.yuanToCent(refundOrder.getPaidAmount()));
            refundChannelRequest.setPayChannelStreamId(refundOrder.getPayChannelStreamId());
            refundChannelRequest.setPayOrderId(refundOrder.getPayOrderId());
            refundChannelRequest.setRefundAmount(MoneyUtils.yuanToCent(refundOrder.getApplyRefundAmount()));
            refundChannelRequest.setRefundReason(refundOrder.getMemo());

            // 调用第三方服务, 进行异步退款
            RefundChannelResponse response = payChannelServiceFactory.get(paySuccessEvent.getPayChannel()).refund(refundChannelRequest);

            // 推进退款单状态为 Refunding
            if (response.getSuccess()) {
                refundOrderService.refunding(refundOrder.getRefundOrderId());
            }
        });
    }

    private OrderPayRequest getOrderPayRequest(PaySuccessEvent paySuccessEvent, PayOrder payOrder) {
        OrderPayRequest orderPayRequest = new OrderPayRequest();
        orderPayRequest.setOperateTime(paySuccessEvent.getPaySucceedTime());
        orderPayRequest.setPayChannel(paySuccessEvent.getPayChannel());
        orderPayRequest.setPayStreamId(payOrder.getPayOrderId());
        orderPayRequest.setAmount(paySuccessEvent.getPaidAmount());
        orderPayRequest.setOrderId(payOrder.getBizNo());
        orderPayRequest.setOperatorType(payOrder.getPayerType());
        orderPayRequest.setOperator(payOrder.getPayerId());
        orderPayRequest.setIdentifier(payOrder.getPayOrderId());

        return orderPayRequest;
    }

    private GoodsSaleRequest getGoodsSaleRequest(TradeOrderVO tradeOrderVO) {
        GoodsSaleRequest goodsSaleRequest = new GoodsSaleRequest();
        goodsSaleRequest.setGoodsId(Long.valueOf(tradeOrderVO.getGoodsId()));
        goodsSaleRequest.setGoodsType(tradeOrderVO.getGoodsType().name());
        goodsSaleRequest.setIdentifier(tradeOrderVO.getOrderId());
        goodsSaleRequest.setUserId(tradeOrderVO.getBuyerId());
        goodsSaleRequest.setQuantity(tradeOrderVO.getItemCount());
        goodsSaleRequest.setBizNo(tradeOrderVO.getOrderId());
        goodsSaleRequest.setBizType(GoodsSaleBizType.PRIMARY_TRADE.name());
        goodsSaleRequest.setName(tradeOrderVO.getGoodsName());
        goodsSaleRequest.setCover(tradeOrderVO.getGoodsPicUrl());
        goodsSaleRequest.setPurchasePrice(tradeOrderVO.getItemPrice());

        return goodsSaleRequest;
    }
}
