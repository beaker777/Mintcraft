package com.beaker.mintcraft.api.order.request;

import com.beaker.mintcraft.api.order.constant.TradeOrderEvent;
import com.beaker.mintcraft.api.user.constant.UserType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/5/19 15:08
 * @Description 订单创建并确认请求
 */
@Data
public class OrderCreateAndConfirmRequest extends OrderCreateRequest {

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

    /**
     * 是否同步扣减库存
     */
    private boolean syncDecreaseInventory = false;

    @Override
    public TradeOrderEvent getOrderEvent() {
        return TradeOrderEvent.CREATE_AND_CONFIRM;
    }
}
