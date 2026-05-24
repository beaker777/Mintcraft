package com.beaker.mintcraft.order.facade;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beaker.mintcraft.api.goods.request.GoodsSaleRequest;
import com.beaker.mintcraft.api.goods.response.GoodsSaleResponse;
import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.api.order.request.*;
import com.beaker.mintcraft.api.order.request.base.BaseOrderUpdateRequest;
import com.beaker.mintcraft.api.order.response.OrderResponse;
import com.beaker.mintcraft.api.order.service.OrderFacadeService;
import com.beaker.mintcraft.api.order.valobj.TradeOrderVO;
import com.beaker.mintcraft.api.user.constant.UserType;
import com.beaker.mintcraft.api.user.request.UserQueryRequest;
import com.beaker.mintcraft.api.user.response.UserQueryResponse;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.lock.DistributeLock;
import com.beaker.mintcraft.mq.producer.StreamProducer;
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import com.beaker.mintcraft.order.domain.entity.convertor.TradeOrderConvertor;
import com.beaker.mintcraft.order.domain.service.OrderManageService;
import com.beaker.mintcraft.order.domain.service.OrderService;
import com.beaker.mintcraft.order.exception.OrderException;
import com.beaker.mintcraft.order.validator.OrderCreateValidator;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.*;

/**
 * @Author beaker
 * @Date 2026/5/12 20:54
 * @Description 订单 facade 层实现类
 */
@DubboService
public class OrderFacadeServiceImpl implements OrderFacadeService {

    @Resource
    private OrderService orderService;

    @Resource
    private OrderManageService orderManageService;

    @Resource
    private UserFacadeService userFacadeService;

    @Resource
    private InventoryFacadeService inventoryFacadeService;

    @Resource
    private GoodsFacadeService goodsFacadeService;

    @Resource
    private OrderCreateValidator orderValidatorChain;

    @Resource
    private OrderCreateValidator orderConfirmValidatorChain;

    @Resource
    private StreamProducer streamProducer;

    @Override
    public SingleResponse<TradeOrderVO> getTradeOrder(String orderId) {
        return SingleResponse.of(TradeOrderConvertor.INSTANCE.mapToVo(orderService.getOrder(orderId)));
    }

    @Override
    public SingleResponse<TradeOrderVO> getTradeOrder(String orderId, String userId) {
        return SingleResponse.of(TradeOrderConvertor.INSTANCE.mapToVo(orderService.getOrder(orderId, userId)));
    }

    @Override
    public PageResponse<TradeOrderVO> pageQuery(OrderPageQueryRequest request) {
        Page<TradeOrder> page = orderService.pageQueryByState(
                request.getBuyerId(), request.getState(), request.getCurrentPage(), request.getPageSize());

        // 获取 sellerName
        List<TradeOrderVO> tradeOrderVOs = TradeOrderConvertor.INSTANCE.mapToVo(page.getRecords());
        tradeOrderVOs.forEach(tradeOrderVO -> tradeOrderVO.setSellerName(getSellerName(tradeOrderVO)));

        return PageResponse.of(tradeOrderVOs, (int) page.getTotal(), request.getPageSize(), request.getCurrentPage());
    }

    @Override
    @DistributeLock(keyExpression = "#request.identifier", scene = "ORDER_CREATE")
    public OrderResponse create(OrderCreateRequest request) {
        // TODO: 后续补充 sentinel 相关
        // 校验订单创建请求
        try {
            orderValidatorChain.validate(request);
        } catch (OrderException e) {
            return new OrderResponse.OrderResponseBuilder()
                    .buildFail(ORDER_CREATE_VALID_FAILED.getCode(), e.getErrorCode().getMessage());
        }

        // 扣减 Redis 藏品库存
        InventoryRequest inventoryRequest = new InventoryRequest(request);
        SingleResponse<Boolean> decreaseResult = inventoryFacadeService.decrease(inventoryRequest);

        if (decreaseResult.getSuccess()) {
            // 创建订单并异步确认订单
            return orderManageService.createAndAsyncConfirm(request);
        }

        // 扣减库存失败
        throw new OrderException(INVENTORY_DECREASE_FAILED);
    }

    @Override
    public OrderResponse confirm(OrderConfirmRequest request) {
        // 扣减数据库藏品库存
        GoodsSaleRequest goodsSaleRequest = new GoodsSaleRequest();
        goodsSaleRequest.setUserId(request.getBuyerId());
        goodsSaleRequest.setGoodsId(Long.valueOf(request.getGoodsId()));
        goodsSaleRequest.setGoodsType(request.getGoodsType().name());
        goodsSaleRequest.setIdentifier(request.getOrderId());
        goodsSaleRequest.setQuantity(request.getItemCount());
        GoodsSaleResponse response = goodsFacadeService.sale(goodsSaleRequest);

        if (response.getSuccess()) {
            // 确认订单
            return orderManageService.confirm(request);
        }

        // 确认订单失败
        return new OrderResponse.OrderResponseBuilder().orderId(request.getOrderId())
                .buildFail(response.getResponseCode(), response.getResponseMessage());
    }

    @Override
    @DistributeLock(keyExpression = "#request.identifier", scene = "ORDER_CREATE")
    public OrderResponse createAndConfirm(OrderCreateAndConfirmRequest request) {
        // 校验订单创建请求
        try {
            orderConfirmValidatorChain.validate(request);
        } catch (OrderException e) {
            return new OrderResponse.OrderResponseBuilder()
                    .orderId(request.getOrderId())
                    .buildFail(ORDER_CREATE_VALID_FAILED.getCode(), ORDER_CREATE_VALID_FAILED.getMessage());
        }

        if (request.isSyncDecreaseInventory()) {
            GoodsSaleRequest goodsSaleRequest = new GoodsSaleRequest(request);
            GoodsSaleResponse response = goodsFacadeService.sale(goodsSaleRequest);

            if (!response.getSuccess()) {
                return new OrderResponse.OrderResponseBuilder().buildFail(response.getResponseCode(), response.getResponseMessage());
            }
        }

        return orderManageService.createAndConfirm(request);
    }

    @Override
    public OrderResponse cancel(OrderCancelRequest request) {
        return sendTransactionMsgForClose(request);
    }

    @Override
    public OrderResponse timeout(OrderTimeoutRequest request) {
        return sendTransactionMsgForClose(request);
    }

    @Override
    public OrderResponse paySuccess(OrderPayRequest request) {
        OrderResponse orderResponse = orderManageService.paySuccess(request);

        // 如果订单状态更新失败, 根据订单状态返回 error
        if (!orderResponse.getSuccess()) {
            TradeOrder existOrder = orderService.getOrder(request.getOrderId());

            if (existOrder != null && existOrder.isClosed()) {
                return new OrderResponse.OrderResponseBuilder()
                        .orderId(existOrder.getOrderId())
                        .buildFail(ORDER_ALREADY_CLOSED.getCode(), ORDER_ALREADY_CLOSED.getMessage());
            }
            if (existOrder != null && existOrder.isPaid()) {
                // 如果订单已支付, 且由本次请求支付, 返回支付成功
                if (existOrder.getPayStreamId().equals(request.getPayStreamId()) && existOrder.getPayChannel() == request.getPayChannel()) {
                    return new OrderResponse.OrderResponseBuilder().orderId(existOrder.getOrderId()).buildSuccess();
                } else {
                    return new OrderResponse.OrderResponseBuilder()
                            .orderId(existOrder.getOrderId())
                            .buildFail(ORDER_ALREADY_PAID.getCode(), ORDER_ALREADY_PAID.getMessage());
                }
            }
        }

        return orderResponse;
    }

    private OrderResponse sendTransactionMsgForClose(BaseOrderUpdateRequest request) {
        // 本地事务执行器: OrderCloseTransactionListener
        // 消息监听: TradeOrderListener
        streamProducer.send("orderClose-out-0", null, JSON.toJSONString(request), "CLOSE_TYPE", request.getOrderEvent().name());

        // 因为 MQ 只要发送成功 msg 就会返回 true, 需要反查一遍确保已经关闭订单了
        TradeOrder tradeOrder = orderService.getOrder(request.getOrderId());
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setSuccess(tradeOrder.isClosed());

        return orderResponse;
    }

    private String getSellerName(TradeOrderVO tradeOrderVO) {
        // 如果卖家类型为平台直接返回
        if (tradeOrderVO.getSellerType() == UserType.PLATFORM) {
            return "平台";
        }

        // 如果卖家类型为 user rpc 获取到用户名
        UserQueryRequest userQueryRequest = new UserQueryRequest(Long.valueOf(tradeOrderVO.getSellerId()));
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
        if (userQueryResponse.getSuccess()) {
            return userQueryResponse.getData().getNickName();
        }

        // 没查询到, 返回默认字符串
        return "-";
    }
}
