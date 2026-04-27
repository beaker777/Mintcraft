package com.beaker.mintcraft.base.exception;

/**
 * @Author beaker
 * @Date 2026/4/26 19:12
 * @Description 错误码
 */
public interface ErrorCode {

    /**
     * 错误码
     *
     * @return 错误码
     */
    String getCode();

    /**
     * 错误信息
     *
     * @return 错误信息
     */
    String getMessage();
}
