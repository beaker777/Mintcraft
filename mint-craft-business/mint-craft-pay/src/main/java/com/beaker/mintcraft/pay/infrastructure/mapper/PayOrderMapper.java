package com.beaker.mintcraft.pay.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.pay.domain.entity.PayOrder;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author beaker
 * @Date 2026/5/23 15:50
 * @Description 支付单 mapper
 */
@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder> {

    /**
     * 根据 bizNo 和 payer 查询
     *
     * @param payerId
     * @param bizNo
     * @param bizType
     * @param payChannel
     * @return
     */
    PayOrder selectByBizNoAndPayer(String payerId, String bizNo, String bizType, String payChannel);

    /**
     * 根据payOrderId查询
     *
     * @param payOrderId
     * @return
     */
    PayOrder selectByPayOrderId(@NotNull String payOrderId);
}
