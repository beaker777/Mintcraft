package com.beaker.mintcraft.pay.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.pay.domain.entity.RefundOrder;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author beaker
 * @Date 2026/5/31 15:32
 * @Description 退款单 Mapper
 */
@Mapper
public interface RefundOrderMapper extends BaseMapper<RefundOrder> {

    /**
     * 根据幂等条件查询退款单
     *
     * @param payOrderId
     * @param identifier
     * @param refundChannel
     * @return
     */
    RefundOrder selectByIdentifier(String payOrderId, String identifier, String refundChannel);

    /**
     * 根据refundOrderId查询
     *
     * @param refundOrderId
     * @return
     */
    RefundOrder selectByRefundOrderId(@NotNull String refundOrderId);
}
