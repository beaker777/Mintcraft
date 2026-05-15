package com.beaker.mintcraft.order.domain.entity.convertor;

import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.api.order.valobj.TradeOrderVO;
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/5/12 21:12
 * @Description 订单转换类
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TradeOrderConvertor {

    TradeOrderConvertor INSTANCE = Mappers.getMapper(TradeOrderConvertor.class);

    /**
     * 转换vo
     *
     * @param request
     * @return
     */
    @Mapping(target = "timeout", expression = "java(request.isTimeout())")
    public TradeOrderVO mapToVo(TradeOrder request);

    /**
     * 转换vo
     *
     * @param request
     * @return
     */
    public List<TradeOrderVO> mapToVo(List<TradeOrder> request);

    /**
     * 转换实体
     *
     * @param request
     * @return
     */
    public TradeOrder mapToEntity(OrderCreateRequest request);
}
