package com.beaker.mintcraft.order.exception;

import com.beaker.mintcraft.base.exception.ErrorCode;
import com.beaker.mintcraft.base.exception.biz.BizException;

/**
 * @Author beaker
 * @Date 2026/5/14 15:59
 * @Description 订单异常
 */
public class OrderException extends BizException {

    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OrderException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public OrderException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public OrderException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public OrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
