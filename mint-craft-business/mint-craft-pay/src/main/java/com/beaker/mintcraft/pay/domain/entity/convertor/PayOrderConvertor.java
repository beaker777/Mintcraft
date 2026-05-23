package com.beaker.mintcraft.pay.domain.entity.convertor;

import com.beaker.mintcraft.api.pay.request.PayCreateRequest;
import com.beaker.mintcraft.api.pay.valobj.PayOrderVO;
import com.beaker.mintcraft.pay.domain.entity.PayOrder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/5/23 16:02
 * @Description 支付单转换器
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PayOrderConvertor {

    PayOrderConvertor INSTANCE = Mappers.getMapper(PayOrderConvertor.class);

    /**
     * 转换实体
     *
     * @param request
     * @return
     */
    public PayOrder mapToEntity(PayCreateRequest request);

    /**
     * 转换vo
     *
     * @param request
     * @return
     */
    public PayOrderVO mapToVo(PayOrder request);

    /**
     * 转换vo
     *
     * @param request
     * @return
     */
    public List<PayOrderVO> mapToVo(List<PayOrder> request);
}
