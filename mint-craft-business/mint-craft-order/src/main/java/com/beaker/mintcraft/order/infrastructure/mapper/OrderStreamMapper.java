package com.beaker.mintcraft.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.order.domain.entity.TradeOrderStream;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author beaker
 * @Date 2026/5/15 18:38
 * @Description 订单流水 mapper
 */
@Mapper
public interface OrderStreamMapper extends BaseMapper<TradeOrderStream> {

}
