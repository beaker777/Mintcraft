package com.beaker.mintcraft.pay.domain.event;

import com.beaker.mintcraft.api.pay.constant.PayChannel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/5/31 16:29
 * @Description 退款成功事件
 */
@Data
public class RefundSuccessEvent {

    /**
     * 支付单号
     */
    private String payOrderId;

    /**
     * 退款单号
     */
    private String refundOrderId;

    /**
     * 退款成功时间
     */
    private Date refundedTime;

    /**
     * 渠道流水号
     */
    private String channelStreamId;

    /**
     * 退款金额
     */
    private BigDecimal refundedAmount;

    /**
     * 退款渠道
     */
    private PayChannel refundChannel;
}
