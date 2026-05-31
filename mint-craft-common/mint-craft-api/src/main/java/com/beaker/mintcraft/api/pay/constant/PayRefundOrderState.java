package com.beaker.mintcraft.api.pay.constant;

/**
 * @Author beaker
 * @Date 2026/5/31 15:28
 * @Description 退款单状态
 */
public enum PayRefundOrderState {

    /**
     * 待退款
     */
    TO_REFUND,

    /**
     * 退款中
     */
    REFUNDING,

    /**
     * 已退款
     */
    REFUNDED;
}
