package com.beaker.mintcraft.trade.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.beaker.mintcraft.api.common.constant.BusinessCode;
import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.goods.valobj.BaseGoodsVO;
import com.beaker.mintcraft.api.inventory.request.InventoryCheckRequest;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.response.InventoryCheckResponse;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.api.order.response.OrderResponse;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.mq.producer.StreamProducer;
import com.beaker.mintcraft.order.exception.OrderException;
import com.beaker.mintcraft.order.sharding.id.DistributeID;
import com.beaker.mintcraft.order.sharding.id.WorkerIdHolder;
import com.beaker.mintcraft.order.validator.OrderCreateValidator;
import com.beaker.mintcraft.rpc.support.RemoteCallWrapper;
import com.beaker.mintcraft.trade.infrastructure.exception.TradeErrorCode;
import com.beaker.mintcraft.trade.infrastructure.exception.TradeException;
import com.beaker.mintcraft.trade.param.BuyParam;
import com.beaker.mintcraft.web.vo.Result;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.beaker.mintcraft.trade.infrastructure.exception.TradeErrorCode.ORDER_CREATE_FAILED;
import static com.beaker.mintcraft.web.filter.TokenFilter.TOKEN_THREAD_LOCAL;

/**
 * @Author beaker
 * @Date 2026/5/16 18:23
 * @Description 交易接口
 */
@Slf4j
@RestController
@RequestMapping("/trade")
public class TradeController {

    private static ThreadFactory inventoryByPassVerifyThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("inventory-bypass-verify-pool-%d")
            .build();

    private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(10, inventoryByPassVerifyThreadFactory);

    @Autowired
    private GoodsFacadeService goodsFacadeService;

    @Autowired
    private OrderFacadeService orderFacadeService;

    @Autowired
    private InventoryFacadeService inventoryFacadeService;

    @Autowired
    private OrderCreateValidator orderValidatorChain;

    @Autowired
    private StreamProducer streamProducer;

    /**
     * 下单
     * 秒杀下单，热点商品
     *
     * @param
     * @return 订单号
     */
    @PostMapping("/buy")
    public Result<String> buy(@Valid @RequestBody BuyParam buyParam) {
        try {
            OrderCreateRequest orderCreateRequest = getOrderCreateRequest(buyParam);

            // rpc 调用订单 create 方法
            OrderResponse orderResponse = RemoteCallWrapper.call(req -> orderFacadeService.create(req),
                    orderCreateRequest, "createOrder");

            if (orderResponse.getSuccess()) {
                // 3s 后旁路校验库存扣减
                InventoryRequest inventoryRequest = new InventoryRequest(orderCreateRequest);
                inventoryByPassVerify(inventoryRequest);

                // 校验成功, 返回订单 id
                return Result.success(orderResponse.getOrderId());
            }
        } catch (OrderException | TradeException e) {
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return Result.error(ORDER_CREATE_FAILED.getCode(), ORDER_CREATE_FAILED.getMessage());
    }

    @PostMapping("/newBuy")
    public Result<String> newBuy(@Valid @RequestBody BuyParam buyParam) {
        try {
            OrderCreateRequest orderCreateRequest = getOrderCreateRequest(buyParam);

            // 校验订单创建请求
            orderValidatorChain.validate(orderCreateRequest);

            //
            boolean result = streamProducer
                    .send("newBuy-out-0", buyParam.getGoodsType(), JSON.toJSONString(orderCreateRequest));

        } catch (Exception e) {

        }

        return null;
    }

    /**
     * 订单创建请求构建
     *
     * @param buyParam
     * @return
     */
    @NotNull
    private OrderCreateRequest getOrderCreateRequest(BuyParam buyParam) {
        String userId = (String) StpUtil.getLoginId();
        String orderId = DistributeID.generateWithSnowflake(BusinessCode.TRADE_ORDER, WorkerIdHolder.WORKER_ID, userId);

        // 创建订单
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setOrderId(orderId);
        orderCreateRequest.setIdentifier(TOKEN_THREAD_LOCAL.get());
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
        orderCreateRequest.setGoodsName(goodsVO.getGoodsName());
        orderCreateRequest.setGoodsPicUrl(goodsVO.getGoodsPicUrl());
        orderCreateRequest.setSnapshotVersion(goodsVO.getVersion());

        // 计算商品总价
        orderCreateRequest.setOrderAmount(
                orderCreateRequest.getItemPrice().multiply(new BigDecimal(orderCreateRequest.getItemCount())));

        return orderCreateRequest;
    }

    /**
     * 数据库库存扣减旁路校验
     *
     * @param inventoryRequest
     */
    private void inventoryByPassVerify(InventoryRequest inventoryRequest) {
        try {
            // 延迟 3s 后校验数据库是否有库存扣减记录
            scheduler.schedule(() -> {
                InventoryCheckRequest inventoryCheckRequest = new InventoryCheckRequest();
                inventoryCheckRequest.setIdentifier(inventoryRequest.getIdentifier());
                inventoryCheckRequest.setGoodsType(inventoryRequest.getGoodsType());
                inventoryCheckRequest.setGoodsId(inventoryRequest.getGoodsId());
                inventoryCheckRequest.setGoodsEvent(GoodsEvent.TRY_SALE);
                inventoryCheckRequest.setChangedQuantity(inventoryRequest.getInventory());

                InventoryCheckResponse checkResponse = inventoryFacadeService.check(inventoryCheckRequest);
                // 核验成功
                if (checkResponse.getSuccess() && checkResponse.getCheckResult()) {
                    // 删除库存扣减流水记录
                    inventoryFacadeService.removeInventoryDecreaseLog(inventoryRequest);
                }
            }, 3, TimeUnit.SECONDS);
        } catch (Exception e) {
            // 打印失败日志, 不影响主流程, 等待异步任务核对
            log.error("inventoryByPassVerify failed, ", e);
        }
    }
}
