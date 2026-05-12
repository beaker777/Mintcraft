package com.beaker.mintcraft.api.order.constant;

/**
 * @Author beaker
 * @Date 2026/5/12 20:47
 * @Description 订单状态
 */
public enum TradeOrderState {

    /**
     * 订单创建
     */
    CREATE,

    /**
     * 订单确认
     */
    CONFIRM,

    /**
     * 已付款
     */
    PAID,

    /**
     * 交易成功
     */
    FINISH,

    /**
     * 订单关闭
     */
    CLOSED,

    /**
     * 已废弃
     */
    DISCARD;
}
