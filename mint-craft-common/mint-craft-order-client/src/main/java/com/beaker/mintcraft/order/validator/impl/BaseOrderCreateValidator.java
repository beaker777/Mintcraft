package com.beaker.mintcraft.order.validator.impl;

import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.order.exception.OrderException;
import com.beaker.mintcraft.order.validator.OrderCreateValidator;

/**
 * @Author beaker
 * @Date 2026/5/14 16:03
 * @Description 订单创建校验抽象类
 */
public abstract class BaseOrderCreateValidator implements OrderCreateValidator {

    protected OrderCreateValidator nextValidator;

    @Override
    public void setNext(OrderCreateValidator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public OrderCreateValidator getNext() {
        return nextValidator;
    }

    /**
     * 校验
     *
     * @param request
     * @throws Exception
     */
    @Override
    public void validate(OrderCreateRequest request) throws OrderException {
        doValidate(request);

        if (nextValidator != null) {
            nextValidator.validate(request);
        }
    }

    /**
     * 校验方法的具体实现
     *
     * @param request
     * @throws OrderException
     */
    protected abstract void doValidate(OrderCreateRequest request) throws OrderException;
}
