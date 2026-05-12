package com.beaker.mintcraft.order.controller;

import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.order.valobj.TradeOrderVO;
import com.beaker.mintcraft.base.response.SingleResponse;
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
    public SingleResponse<TradeOrderVO> getOrder(String orderId) {
        return orderFacadeService.getTradeOrder(orderId);
    }
}
