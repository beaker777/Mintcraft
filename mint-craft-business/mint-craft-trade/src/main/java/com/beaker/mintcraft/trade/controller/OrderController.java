package com.beaker.mintcraft.trade.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.beaker.mintcraft.api.order.constant.TradeOrderState;
import com.beaker.mintcraft.api.order.request.OrderPageQueryRequest;
import com.beaker.mintcraft.api.order.request.OrderTimeoutRequest;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.order.valobj.TradeOrderVO;
import com.beaker.mintcraft.api.pay.service.PayFacadeService;
import com.beaker.mintcraft.api.pay.valobj.PayOrderVO;
import com.beaker.mintcraft.api.user.constant.UserType;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.web.util.MultiResultConvertor;
import com.beaker.mintcraft.web.vo.MultiResult;
import com.beaker.mintcraft.web.vo.Result;
import jakarta.validation.constraints.NotNull;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @Author beaker
 * @Date 2026/6/2 17:54
 * @Description 订单 controller
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @DubboReference
    private OrderFacadeService orderFacadeService;

    @DubboReference
    private PayFacadeService payFacadeService;

    @GetMapping("/orderList")
    public MultiResult<TradeOrderVO> orderList(String state, int pageSize, int currentPage) {
        String userId = (String) StpUtil.getLoginId();

        OrderPageQueryRequest orderPageQueryRequest = new OrderPageQueryRequest();
        orderPageQueryRequest.setBuyerId(userId);
        orderPageQueryRequest.setState(state);
        orderPageQueryRequest.setCurrentPage(currentPage);
        orderPageQueryRequest.setPageSize(pageSize);

        PageResponse<TradeOrderVO> pageResponse = orderFacadeService.pageQuery(orderPageQueryRequest);
        return MultiResultConvertor.convert(pageResponse);
    }

    @GetMapping("/orderDetail")
    public Result<TradeOrderVO> orderDetail(@NotNull String orderId) {
        String userId = (String) StpUtil.getLoginId();

        SingleResponse<TradeOrderVO> response = orderFacadeService.getTradeOrder(orderId, userId);
        if (response.getSuccess()) {
            TradeOrderVO tradeOrderVO = response.getData();

            if (tradeOrderVO == null) {
                return Result.error("ORDER_NOT_EXIST", "订单不存在");
            }
            if (tradeOrderVO.getTimeout() && tradeOrderVO.getOrderState() == TradeOrderState.CONFIRM) {
                // 如果订单超时且未关单, 关单后返回
                OrderTimeoutRequest orderTimeoutRequest = new OrderTimeoutRequest();
                orderTimeoutRequest.setOperatorType(UserType.PLATFORM);
                orderTimeoutRequest.setOperator(UserType.PLATFORM.getDesc());
                orderTimeoutRequest.setOrderId(orderId);
                orderTimeoutRequest.setOperateTime(new Date());
                orderTimeoutRequest.setIdentifier(UUID.randomUUID().toString());

                orderFacadeService.timeout(orderTimeoutRequest);
                response = orderFacadeService.getTradeOrder(orderId, userId);
            }

            return Result.success(response.getData());
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    @GetMapping("/getPayStatus")
    public Result<PayOrderVO> getPayStatus(@NotNull String payOrderId) {
        String userId = (String) StpUtil.getLoginId();

        SingleResponse<PayOrderVO> singleResponse = payFacadeService.queryPayOrder(payOrderId);
        if (singleResponse.getSuccess()) {
            return Result.success(singleResponse.getData());
        } else {
            return Result.error(singleResponse.getResponseCode(), singleResponse.getResponseMessage());
        }
    }
}
