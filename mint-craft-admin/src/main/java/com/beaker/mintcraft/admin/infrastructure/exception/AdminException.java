package com.beaker.mintcraft.admin.infrastructure.exception;

import com.beaker.mintcraft.base.exception.ErrorCode;
import com.beaker.mintcraft.base.exception.biz.BizException;

/**
 * @Author beaker
 * @Date 2026/6/1 18:20
 * @Description admin 异常
 */
public class AdminException extends BizException {

    public AdminException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AdminException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AdminException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public AdminException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public AdminException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
