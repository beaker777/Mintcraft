package com.beaker.mintcraft.pay.domain.event;

import com.beaker.mintcraft.api.pay.constant.PayChannel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/5/23 17:03
 * @Description 支付成功事件
 */
@Data
public class PaySuccessEvent {

    /**
     * 支付订单号
     */
    private String payOrderId;

    /**
     * 支付成功时间
     */
    private Date paySucceedTime;

    /**
     * 渠道流水号
     */
    private String channelStreamId;

    /**
     * 支付渠道
     */
    private PayChannel payChannel;

    /**
     * 支付金额
     */
    private BigDecimal paidAmount;
}
