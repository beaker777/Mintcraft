package com.beaker.mintcraft.api.pay.request;

import com.beaker.mintcraft.api.pay.constant.PayOrderState;
import com.beaker.mintcraft.api.pay.request.condition.PayQueryCondition;
import com.beaker.mintcraft.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/31 18:39
 * @Description 支付单查询请求
 */
@Data
public class PayQueryRequest extends BaseRequest {

    @NotNull(message = "payQueryCondition is null")
    private PayQueryCondition payQueryCondition;

    private PayOrderState payOrderState;

    @NotNull(message = "payerId is null")
    private String payerId;
}
