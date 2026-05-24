package com.beaker.mintcraft.chain.domain.constant;

/**
 * @Author beaker
 * @Date 2026/5/24 18:15
 * @Description 链操作码
 */
public enum ChainCode {

    /**
     * 上链成功
     */
    SUCCESS,

    /**
     * 上链中
     */
    PROCESSING,

    /**
     * 上链请求异常
     */
    CHAIN_POST_ERROR,

    /**
     * 上链返回结果不是json
     */
    CHAIN_RESULT_NOT_JSON,

    /**
     * 上链返回结果错误
     */
    CHAIN_RESULT_ERROR,
}
