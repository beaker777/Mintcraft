package com.beaker.mintcraft.chain.domain.constant;

/**
 * @Author beaker
 * @Date 2026/5/25 20:48
 * @Description 链操作状态
 */
public enum ChainOperateState {

    /**
     * 上链成功
     */
    SUCCEED,

    /**
     * 上链中
     */
    PROCESSING,

    /**
     * 上链失败
     */
    FAILED,

    /**
     * 未处理
     */
    INIT
}
