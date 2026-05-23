package com.beaker.mintcraft.api.pay.request;

import com.beaker.mintcraft.api.common.constant.BizOrderType;
import com.beaker.mintcraft.api.pay.constant.PayChannel;
import com.beaker.mintcraft.api.user.constant.UserType;
import com.beaker.mintcraft.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author beaker
 * @Date 2026/5/23 15:28
 * @Description 支付单创建请求
 */
@Data
public class PayCreateRequest extends BaseRequest {

    /**
     * 付款方 id
     */
    @NotNull(message = "payerId is null")
    private String payerId;

    /**
     * 付款方类型
     */
    @NotNull(message = "payerType is null")
    private UserType payerType;

    /**
     * 收款方 id
     */
    @NotNull(message = "payeeId is null")
    private String payeeId;

    /**
     * 收款方类型
     */
    @NotNull(message = "payeeType is null")
    private UserType payeeType;

    /**
     * 业务单号
     */
    @NotNull(message = "bizNo is null")
    private String bizNo;

    /**
     * 业务单号类型
     */
    @NotNull(message = "bizType is null")
    private BizOrderType bizType;

    /**
     * 订单金额
     */
    @NotNull(message = "orderAmount is null")
    private BigDecimal orderAmount;

    /**
     * 支付渠道
     */
    @NotNull(message = "payChannel is null")
    private PayChannel payChannel;

    /**
     * 支付备注
     */
    @NotNull(message = "memo is null")
    private String memo;
}
