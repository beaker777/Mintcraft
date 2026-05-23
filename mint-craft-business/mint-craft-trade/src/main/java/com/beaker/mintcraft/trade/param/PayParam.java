package com.beaker.mintcraft.trade.param;

import com.beaker.mintcraft.api.pay.constant.PayChannel;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/22 21:09
 * @Description 支付参数
 */
@Data
public class PayParam {

    @NotNull(message = "orderId is null")
    private String orderId;

    @NotNull(message = "payChannel is null")
    private PayChannel payChannel;

}
