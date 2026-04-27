package com.beaker.mintcraft.base.exception;

/**
 * @Author beaker
 * @Date 2026/4/27 17:16
 * @Description 系统异常错误码
 */
public enum SystemErrorCode implements ErrorCode {

    /**
     * 远程调用返回结果为空
     */
    REMOTE_CALL_RESPONSE_IS_NULL("REMOTE_CALL_RESPONSE_IS_NULL", "远程调用返回结果为空"),

    /**
     * 远程调用返回结果失败
     */
    REMOTE_CALL_RESPONSE_IS_FAILED("REMOTE_CALL_RESPONSE_IS_FAILED", "远程调用返回结果失败");


    private String errorCode;

    private String errorMessage;

    SystemErrorCode(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getCode() {
        return this.errorCode;
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }
}
