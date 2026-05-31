package com.beaker.mintcraft.api.pay.request;

import com.beaker.mintcraft.api.pay.constant.PayChannel;
import com.beaker.mintcraft.base.request.BaseRequest;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author beaker
 * @Date 2026/5/31 15:14
 * @Description 退款创建请求
 */
@Data
public class RefundCreateRequest extends BaseRequest {

    /**
     * 支付单号
     */
    private String payOrderId;

    /**
     * 需要退款的金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款幂等号
     */
    private String identifier;

    /**
     * 退款渠道
     */
    private PayChannel refundChannel;

    /**
     * 退款备注
     */
    private String memo;
}
