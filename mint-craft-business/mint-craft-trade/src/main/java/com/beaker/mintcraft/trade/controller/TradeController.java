package com.beaker.mintcraft.trade.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.beaker.mintcraft.api.common.constant.BusinessCode;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.goods.valobj.BaseGoodsVO;
import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.order.sharding.id.DistributeID;
import com.beaker.mintcraft.order.sharding.id.WorkerIdHolder;
import com.beaker.mintcraft.order.validator.OrderCreateValidator;
import com.beaker.mintcraft.trade.infrastructure.exception.TradeErrorCode;
import com.beaker.mintcraft.trade.infrastructure.exception.TradeException;
import com.beaker.mintcraft.trade.param.BuyParam;
import com.beaker.mintcraft.web.vo.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @Author beaker
 * @Date 2026/5/16 18:23
 * @Description 交易接口
 */
@RestController
@RequestMapping("/order")
public class TradeController {

    @Autowired
    private GoodsFacadeService goodsFacadeService;

    @Autowired
    private OrderCreateValidator orderValidatorChain;

    @PostMapping("/buy")
    public Result<String> buy(@Valid @RequestBody BuyParam buyParam) {
        OrderCreateRequest orderCreateRequest = null;

        try {
            // 构造并校验创建订单请求
            orderCreateRequest = getOrderCreateRequest(buyParam);
            orderValidatorChain.validate(orderCreateRequest);

            // TODO: 接入 MQ
        } catch (Exception e) {

        }

        return null;
    }

    @NotNull
    private OrderCreateRequest getOrderCreateRequest(BuyParam buyParam) {
        String userId = (String) StpUtil.getLoginId();
        String orderId = DistributeID.generateWithSnowflake(BusinessCode.TRADE_ORDER, WorkerIdHolder.WORKER_ID, userId);

        // 创建订单
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setOrderId(orderId);
        // TODO: 这里后续要使用 token
        orderCreateRequest.setIdentifier(orderId);
        orderCreateRequest.setBuyerId(userId);
        orderCreateRequest.setGoodsId(buyParam.getGoodsId());
        orderCreateRequest.setGoodsType(GoodsType.valueOf(buyParam.getGoodsType()));
        orderCreateRequest.setItemCount(buyParam.getItemCount());

        // 获取商品
        BaseGoodsVO goodsVO = goodsFacadeService.getGoods(buyParam.getGoodsId(), GoodsType.valueOf(buyParam.getGoodsType()));
        if (goodsVO == null || !goodsVO.available()) {
            throw new TradeException(TradeErrorCode.GOODS_NOT_FOR_SALE);
        }
        orderCreateRequest.setItemPrice(goodsVO.getPrice());
        orderCreateRequest.setSellerId(goodsVO.getSellerId());
        orderCreateRequest.setSellerId(goodsVO.getGoodsName());
        orderCreateRequest.setGoodsPicUrl(goodsVO.getGoodsPicUrl());
        orderCreateRequest.setSnapshotVersion(goodsVO.getVersion());

        // 计算商品总价
        orderCreateRequest.setOrderAmount(
                orderCreateRequest.getItemPrice().multiply(new BigDecimal(orderCreateRequest.getItemCount())));

        return orderCreateRequest;
    }
}
