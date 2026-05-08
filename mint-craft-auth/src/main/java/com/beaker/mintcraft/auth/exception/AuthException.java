package com.beaker.mintcraft.auth.exception;

import com.beaker.mintcraft.base.exception.ErrorCode;
import com.beaker.mintcraft.base.exception.biz.BizException;

/**
 * @Author beaker
 * @Date 2026/5/8 19:21
 * @Description 登录异常
 */
public class AuthException extends BizException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AuthException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public AuthException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public AuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
