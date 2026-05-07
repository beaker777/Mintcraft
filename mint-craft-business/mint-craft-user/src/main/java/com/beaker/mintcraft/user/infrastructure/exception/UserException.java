package com.beaker.mintcraft.user.infrastructure.exception;

import com.beaker.mintcraft.base.exception.biz.BizException;
import com.beaker.mintcraft.base.exception.ErrorCode;

/**
 * @Author beaker
 * @Date 2026/4/28 18:42
 * @Description 用户模块异常
 */
public class UserException extends BizException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UserException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public UserException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public UserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
