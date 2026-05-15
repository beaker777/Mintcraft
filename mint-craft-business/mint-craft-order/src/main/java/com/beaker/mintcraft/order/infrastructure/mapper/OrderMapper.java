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

    /**
     * 根据订单号和买家ID查询订单
     *
     * @param orderId 订单号
     * @param buyerId 买家ID
     * @return 订单
     */
    TradeOrder selectByOrderIdAndBuyer(@NotNull String orderId, @NotNull String buyerId);

    /**
     * 根据幂等号查询订单
     *
     * @param identifier 幂等号
     * @param buyerId    买家ID
     * @return 订单
     */
    TradeOrder selectByIdentifier(@NotNull String identifier, @NotNull String buyerId);

    /**
     * 更新订单
     *
     * @param tradeOrder
     * @return
     */
    int updateByOrderId(TradeOrder tradeOrder);
}
