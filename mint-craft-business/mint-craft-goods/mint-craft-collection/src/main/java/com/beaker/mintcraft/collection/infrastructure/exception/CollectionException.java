package com.beaker.mintcraft.collection.infrastructure.exception;

import com.beaker.mintcraft.base.exception.ErrorCode;
import com.beaker.mintcraft.base.exception.biz.BizException;

/**
 * @Author beaker
 * @Date 2026/5/10 19:40
 * @Description TODO
 */
public class CollectionException extends BizException {

    public CollectionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CollectionException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public CollectionException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public CollectionException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public CollectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
