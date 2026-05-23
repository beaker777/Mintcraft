package com.beaker.mintcraft.api.pay.constant;

/**
 * @Author beaker
 * @Date 2026/5/22 21:14
 * @Description 支付单状态
 */
public enum PayOrderState {

    /**
     * 待支付
     */
    TO_PAY,

    /**
     * 支付中
     */
    PAYING,

    /**
     * 已付款
     */
    PAID,

    /**
     * 支付失败
     */
    FAILED,

    /**
     * 支付超时
     */
    EXPIRED,

    /**
     * 已退款
     */
    REFUNDED;

}
