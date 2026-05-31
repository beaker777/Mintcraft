package com.beaker.mintcraft.pay.infrastructure.channel.request;

import com.beaker.mintcraft.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/5/31 16:07
 * @Description 退款渠道请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundChannelRequest extends BaseRequest {

    /**
     * 支付单号
     */
    private String payOrderId;

    /**
     * 外部支付流水号
     */
    private String payChannelStreamId;

    /**
     * 退款单号
     */
    private String refundOrderId;

    /**
     * 原支付金额
     * 单位：分
     */
    private Long paidAmount;

    /**
     * 退款金额
     * 单位：分
     */
    private Long refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;
}
