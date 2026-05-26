package com.beaker.mintcraft.chain.infrastructure.exception;

import com.beaker.mintcraft.base.exception.ErrorCode;

/**
 * @Author beaker
 * @Date 2026/5/26 20:17
 * @Description 链异常码
 */
public enum ChainErrorCode implements ErrorCode {

    /**
     * 区块链查询失败
     */
    CHAIN_QUERY_FAIL("CHAIN_QUERY_FAIL", "区块链查询失败"),
    /**
     * 区块链状态不是成功
     */
    CHAIN_PROCESS_STATE_ERROR("CHAIN_PROCESS_STATE_ERROR", "区块链状态不是成功"),
    ;

    private String code;


    private String message;

    ChainErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
