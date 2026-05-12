package com.beaker.mintcraft.order.domain.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import com.beaker.mintcraft.order.infrastructure.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author beaker
 * @Date 2026/5/12 20:55
 * @Description 订单服务
 */
@Service
public class OrderService extends ServiceImpl<OrderMapper, TradeOrder> {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 查询订单信息
     *
     * @param orderId
     * @return
     */
    public TradeOrder getOrder(String orderId) {
        return getById(orderId);
    }
}
