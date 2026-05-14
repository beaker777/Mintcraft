package com.beaker.mintcraft.api.order.request;

import com.beaker.mintcraft.base.request.PageRequest;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/14 14:16
 * @Description 订单分页查询
 */
@Data
public class OrderPageQueryRequest extends PageRequest {

    /**
     * 买家id
     */
    private String buyerId;

    /**
     * 卖家id
     */
    private String sellerId;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 订单状态
     */
    private String state;
}
