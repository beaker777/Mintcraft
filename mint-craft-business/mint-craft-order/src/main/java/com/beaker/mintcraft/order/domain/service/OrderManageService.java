package com.beaker.mintcraft.order.domain.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.api.order.response.OrderResponse;
import com.beaker.mintcraft.base.exception.biz.BizException;
import com.beaker.mintcraft.base.exception.biz.RepoErrorCode;
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import com.beaker.mintcraft.order.domain.entity.TradeOrderStream;
import com.beaker.mintcraft.order.infrastructure.mapper.OrderMapper;
import com.beaker.mintcraft.order.infrastructure.mapper.OrderStreamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author beaker
 * @Date 2026/5/15 18:16
 * @Description 订单操作服务
 */
@Service
public class OrderManageService extends ServiceImpl<OrderMapper, TradeOrder> {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderStreamMapper orderStreamMapper;

    /**
     * 订单创建并异步确认
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createAndAsyncConfirm(OrderCreateRequest request) {
        // 幂等校验
        TradeOrder existOrder = orderMapper.selectByIdentifier(request.getIdentifier(), request.getBuyerId());
        if (existOrder != null) {
            return new OrderResponse.OrderResponseBuilder().orderId(existOrder.getOrderId()).buildSuccess();
        }

        // 将订单保存到数据库并写入流水
        TradeOrder tradeOrder = doCreate(request);

        // TODO: 这里未来要发布一个任务

        return new OrderResponse.OrderResponseBuilder().orderId(tradeOrder.getOrderId()).buildSuccess();
    }

    private TradeOrder doCreate(OrderCreateRequest request) {
        // 创建订单
        TradeOrder tradeOrder = TradeOrder.createOrder(request);

        // 将订单保存到数据库
        boolean result = save(tradeOrder);
        Assert.isTrue(result, () -> new BizException(RepoErrorCode.INSERT_FAILED));

        // 写入订单流水
        TradeOrderStream orderStream = new TradeOrderStream(tradeOrder, request.getOrderEvent(), request.getIdentifier());
        result = (orderStreamMapper.insert(orderStream) == 1);
        Assert.isTrue(result, () -> new BizException(RepoErrorCode.INSERT_FAILED));

        return tradeOrder;
    }
}
