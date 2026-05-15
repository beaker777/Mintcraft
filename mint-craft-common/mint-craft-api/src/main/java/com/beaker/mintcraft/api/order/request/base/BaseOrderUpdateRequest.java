package com.beaker.mintcraft.api.order.request.base;

import com.beaker.mintcraft.api.user.constant.UserType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/5/15 19:59
 * @Description 基本订单更新请求
 */
@Data
public abstract class BaseOrderUpdateRequest extends BaseOrderRequest {

    /**
     * 订单id
     */
    @NotNull(message = "orderId 不能为空")
    private String orderId;

    /**
     * 操作时间
     */
    @NotNull(message = "operateTime 不能为空")
    private Date operateTime;

    /**
     * 操作人
     */
    @NotNull(message = "operator 不能为空")
    private String operator;

    /**
     * 操作人类型
     */
    @NotNull(message = "operatorType 不能为空")
    private UserType operatorType;
}
