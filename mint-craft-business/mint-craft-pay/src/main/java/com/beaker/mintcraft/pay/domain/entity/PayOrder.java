package com.beaker.mintcraft.pay.domain.entity;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.beaker.mintcraft.api.common.constant.BizOrderType;
import com.beaker.mintcraft.api.common.constant.BusinessCode;
import com.beaker.mintcraft.api.pay.constant.PayChannel;
import com.beaker.mintcraft.api.pay.constant.PayOrderState;
import com.beaker.mintcraft.api.pay.request.PayCreateRequest;
import com.beaker.mintcraft.api.user.constant.UserType;
import com.beaker.mintcraft.datasource.domain.entity.BaseEntity;
import com.beaker.mintcraft.pay.domain.entity.convertor.PayOrderConvertor;
import com.beaker.mintcraft.pay.domain.event.PaySuccessEvent;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/5/23 15:47
 * @Description 支付单
 */
@Data
public class PayOrder extends BaseEntity {

    /**
     * 默认超时时间
     */
    public static final int DEFAULT_TIME_OUT_MINUTES = 30;

    /**
     * 支付单号 snowflake 算法生成
     */
    private String payOrderId;

    /**
     * 付款方id
     */
    private String payerId;

    /**
     * 付款方id类型
     */
    private UserType payerType;

    /**
     * 收款方id
     */
    private String payeeId;

    /**
     * 收款方id类型
     */
    private UserType payeeType;

    /**
     * 业务单号 orderId
     */
    private String bizNo;

    /**
     * 业务单号类型
     */
    private BizOrderType bizType;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 已支付金额
     */
    private BigDecimal paidAmount;

    /**
     * 已退款金额
     */
    private BigDecimal refundedAmount;

    /**
     * 外部支付流水号 UUID 生成
     */
    private String channelStreamId;

    /**
     * 退款渠道流水号
     */
    private String refundChannelStreamId;

    /**
     * 支付链接
     */
    private String payUrl;

    /**
     * 支付渠道
     */
    private PayChannel payChannel;

    /**
     * 支付备注
     */
    private String memo;

    /**
     * 订单状态
     */
    private PayOrderState orderState;

    /**
     * 支付成功时间
     */
    private Date paySucceedTime;

    /**
     * 支付失败时间
     */
    private Date payFailedTime;

    /**
     * 支付超时时间
     */
    private Date payExpireTime;

    @JSONField(serialize = false)
    public boolean isPaid() {
        return paidAmount.compareTo(BigDecimal.ZERO) > 0
                && (orderState == PayOrderState.PAID  || orderState == PayOrderState.REFUNDED)
                && channelStreamId != null
                && paySucceedTime != null;
    }

    @JSONField(serialize = false)
    public boolean isPayFailed() {
        return orderState == PayOrderState.FAILED;
    }

    public static PayOrder create(PayCreateRequest payCreateRequest) {
        PayOrder payOrder = PayOrderConvertor.INSTANCE.mapToEntity(payCreateRequest);

        payOrder.setOrderState(PayOrderState.TO_PAY);
        payOrder.setPayOrderId(String.valueOf(IdUtil.getSnowflake(BusinessCode.PAY_ORDER.code()).nextId()));
        payOrder.setPaidAmount(BigDecimal.ZERO);

        return payOrder;
    }


    public PayOrder paying(String payUrl) {
        Assert.equals(this.getOrderState(), PayOrderState.TO_PAY);

        this.setOrderState(PayOrderState.PAYING);
        this.payUrl = payUrl;

        return this;
    }

    public PayOrder paySuccess(PaySuccessEvent paySuccessEvent) {
        Assert.equals(this.getOrderState(), PayOrderState.PAYING);

        this.setOrderState(PayOrderState.PAID);
        this.paySucceedTime = paySuccessEvent.getPaySucceedTime();
        this.channelStreamId = paySuccessEvent.getChannelStreamId();
        this.paidAmount = paySuccessEvent.getPaidAmount();

        return this;
    }
}
