package com.beaker.mintcraft.order.controller;

import com.beaker.mintcraft.api.order.request.OrderPageQueryRequest;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.order.valobj.TradeOrderVO;
import com.beaker.mintcraft.base.response.MultiResponse;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.web.util.MultiResultConvertor;
import com.beaker.mintcraft.web.vo.MultiResult;
import com.beaker.mintcraft.web.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author beaker
 * @Date 2026/5/12 21:28
 * @Description 订单接口
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderFacadeService orderFacadeService;

    @GetMapping("/getOrder")
    public Result<TradeOrderVO> getOrder(String orderId) {
        SingleResponse<TradeOrderVO> response = orderFacadeService.getTradeOrder(orderId);
        return Result.success(response.getData());
    }

    @GetMapping("/getOrderByBuyer")
    public Result<TradeOrderVO> getOrderByBuyer(String orderId, String userId) {
        SingleResponse<TradeOrderVO> response = orderFacadeService.getTradeOrder(orderId, userId);
        return Result.success(response.getData());
    }

    @GetMapping("/pageQuery")
    public MultiResult<TradeOrderVO> pageQuery(@RequestBody OrderPageQueryRequest request) {
        return MultiResultConvertor.convert(orderFacadeService.pageQuery(request));
    }
}
