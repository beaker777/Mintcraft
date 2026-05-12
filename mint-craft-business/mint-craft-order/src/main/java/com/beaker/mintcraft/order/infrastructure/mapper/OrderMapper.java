package com.beaker.mintcraft.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author beaker
 * @Date 2026/5/12 20:57
 * @Description 订单 Mapper 层
 */
@Mapper
public interface OrderMapper extends BaseMapper<TradeOrder> {

    /**
     * 根据订单号查询订单
     *
     * @param orderId 订单号
     * @return 订单
     */
    TradeOrder selectByOrderId(@NotNull String orderId);
}
