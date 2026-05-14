package com.beaker.mintcraft.order.validator;

import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.order.exception.OrderException;

/**
 * @Author beaker
 * @Date 2026/5/14 15:50
 * @Description 订单创建校验类
 */
public interface OrderCreateValidator {

    /**
     * 设置下一个校验器
     *
     * @param nextValidator
     */
    public void setNext(OrderCreateValidator nextValidator);

    /**
     * 返回下一个校验器
     *
     * @return
     */
    public OrderCreateValidator getNext();

    /**
     * 校验
     *
     * @param request
     * @throws OrderException 订单异常
     */
    public void validate(OrderCreateRequest request) throws OrderException;
}
