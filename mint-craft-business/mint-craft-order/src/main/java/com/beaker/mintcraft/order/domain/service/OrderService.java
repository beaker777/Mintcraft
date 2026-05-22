package com.beaker.mintcraft.order.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.api.order.constant.TradeOrderState;
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import com.beaker.mintcraft.order.infrastructure.mapper.OrderMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

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
        return orderMapper.selectByOrderId(orderId);
    }

    /**
     * 查询订单信息
     *
     * @param orderId, userId
     * @return
     */
    public TradeOrder getOrder(String orderId, String userId) {
        return orderMapper.selectByOrderIdAndBuyer(orderId, userId);
    }

    /**
     * 根据订单状态分页查询
     *
     * @param buyerId
     * @param state
     * @param currentPage
     * @param pageSize
     * @return
     */
    public Page<TradeOrder> pageQueryByState(String buyerId, String state, int currentPage, int pageSize) {
        Page<TradeOrder> page = new Page<>();

        // 包装查询条件
        QueryWrapper<TradeOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("buyer_id", buyerId);
        if (StringUtils.isNotBlank(state)) {
            wrapper.eq("order_state", state);
        } else {
            // 查询除了状态为 CREATE 和 DISCARD 外的所有订单
            wrapper.in("order_state",
                    TradeOrderState.CONFIRM.name(), TradeOrderState.PAID.name(),
                    TradeOrderState.FINISH.name(), TradeOrderState.CLOSED.name());
        }
        wrapper.orderBy(true, false, "gmt_create");

        return this.page(page, wrapper);
    }

    public List<TradeOrder> pageQueryTimeoutOrders(int pageSize, @Nullable String buyerIdTailNumber, Long minId) {
        // 包装查询条件
        QueryWrapper<TradeOrder> wrapper = new QueryWrapper<>();
        wrapper.in("order_state", TradeOrderState.CREATE.name(), TradeOrderState.CONFIRM.name());
        wrapper.lt("gmt_create", DateUtils.addMinutes(new Date(), -TradeOrder.DEFAULT_TIME_OUT_MINUTES));
        if (buyerIdTailNumber != null) {
            wrapper.likeRight("reverse_buyer_id", buyerIdTailNumber);
        }
        if (minId != null) {
            wrapper.ge("id", minId);
        }
        wrapper.orderBy(true, true, "gmt_create");
        wrapper.last("limit " + pageSize);

        return this.list(wrapper);
    }
}
