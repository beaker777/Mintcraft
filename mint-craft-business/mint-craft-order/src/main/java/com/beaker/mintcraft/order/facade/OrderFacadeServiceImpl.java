package com.beaker.mintcraft.order.facade;

import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.order.valobj.TradeOrderVO;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.order.domain.entity.convertor.TradeOrderConvertor;
import com.beaker.mintcraft.order.domain.service.OrderService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author beaker
 * @Date 2026/5/12 20:54
 * @Description 订单 facade 层实现类
 */
@DubboService
public class OrderFacadeServiceImpl implements OrderFacadeService {

    @Resource
    private OrderService orderService;

    @Override
    public SingleResponse<TradeOrderVO> getTradeOrder(String orderId) {
        return SingleResponse.of(TradeOrderConvertor.INSTANCE.mapToVo(orderService.getOrder(orderId)));
    }

    @Override
    public SingleResponse<TradeOrderVO> getTradeOrder(String orderId, String userId) {
        return SingleResponse.of(TradeOrderConvertor.INSTANCE.mapToVo(orderService.getOrder(orderId, userId)));
    }
}
