package com.beaker.mintcraft.chain.infrastructure.exception;

import com.beaker.mintcraft.base.exception.ErrorCode;
import com.beaker.mintcraft.base.exception.biz.BizException;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/26 20:16
 * @Description 链异常
 */
@Data
public class ChainException extends BizException {

    public ChainException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChainException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ChainException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public ChainException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public ChainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
