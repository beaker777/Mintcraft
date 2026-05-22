package com.beaker.mintcraft.trade.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/21 21:54
 * @Description 订单取消参数
 */
@Data
public class CancelParam {

    @NotNull(message = "orderId is null")
    private String orderId;
}
