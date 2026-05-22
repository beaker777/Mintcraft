package com.beaker.mintcraft.order.domain.service;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.api.order.constant.TradeOrderEvent;
import com.beaker.mintcraft.api.order.exception.OrderErrorCode;
import com.beaker.mintcraft.api.order.request.*;
import com.beaker.mintcraft.api.order.request.base.BaseOrderUpdateRequest;
import com.beaker.mintcraft.api.order.response.OrderResponse;
import com.beaker.mintcraft.api.user.constant.UserType;
import com.beaker.mintcraft.base.exception.biz.BizException;
import com.beaker.mintcraft.base.exception.biz.RepoErrorCode;
import com.beaker.mintcraft.base.utils.BeanValidator;
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import com.beaker.mintcraft.order.domain.entity.TradeOrderStream;
import com.beaker.mintcraft.order.domain.listener.event.OrderCreateEvent;
import com.beaker.mintcraft.order.exception.OrderException;
import com.beaker.mintcraft.order.infrastructure.mapper.OrderMapper;
import com.beaker.mintcraft.order.infrastructure.mapper.OrderStreamMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.ORDER_NOT_EXIST;
import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.PERMISSION_DENIED;
import static com.beaker.mintcraft.base.response.ResponseCode.SYSTEM_ERROR;
import static java.util.Objects.requireNonNull;

/**
 * @Author beaker
 * @Date 2026/5/15 18:16
 * @Description 订单操作服务
 */
@Service
public class OrderManageService extends ServiceImpl<OrderMapper, TradeOrder> {

    private static final Logger logger = LoggerFactory.getLogger(OrderManageService.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderStreamMapper orderStreamMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 订单创建并异步确认
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createAndAsyncConfirm(OrderCreateRequest request) {
        // 幂等校验
        TradeOrder existOrder = orderMapper.selectByIdentifier(request.getIdentifier(), request.getBuyerId());
        if (existOrder != null) {
            return new OrderResponse.OrderResponseBuilder().orderId(existOrder.getOrderId()).buildSuccess();
        }

        // 将订单保存到数据库并写入流水
        TradeOrder tradeOrder = doCreate(request);

        // 发布任务进行异步确认订单
        applicationContext.publishEvent(new OrderCreateEvent(tradeOrder));

        return new OrderResponse.OrderResponseBuilder().orderId(tradeOrder.getOrderId()).buildSuccess();
    }

    /**
     * 订单确认
     *
     * @param request
     * @return
     */
    public OrderResponse confirm(OrderConfirmRequest request) {
        return doExecute(request, tradeOrder -> tradeOrder.confirm(request));
    }

    /**
     * 订单创建并同步确认
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createAndConfirm(OrderCreateAndConfirmRequest request) {
        // 幂等校验
        TradeOrder existOrder = orderMapper.selectByIdentifier(request.getIdentifier(), request.getBuyerId());
        if (existOrder != null) {
            return new OrderResponse.OrderResponseBuilder().orderId(existOrder.getOrderId()).buildSuccess();
        }

        // 创建订单
        TradeOrder tradeOrder = TradeOrder.createOrder(request);

        // 确认订单
        OrderConfirmRequest confirmRequest = new OrderConfirmRequest();
        BeanUtils.copyProperties(request, confirmRequest);
        tradeOrder.confirm(confirmRequest);

        // 将确认后的订单存入数据库
        boolean result = save(tradeOrder);
        Assert.isTrue(result, () -> new BizException(RepoErrorCode.INSERT_FAILED));

        // 写入流水
        TradeOrderStream orderStream = new TradeOrderStream(tradeOrder, request.getOrderEvent(), request.getIdentifier());
        result = orderStreamMapper.insert(orderStream) == 1;
        Assert.isTrue(result, () -> new BizException(RepoErrorCode.INSERT_FAILED));

        return new OrderResponse.OrderResponseBuilder().orderId(tradeOrder.getOrderId()).buildSuccess();
    }

    /**
     * 主动取消订单
     *
     * @param request
     * @return
     */
    public OrderResponse cancel(OrderCancelRequest request) {
        return doExecute(request, tradeOrder -> tradeOrder.close(request));
    }

    /**
     * 超时关闭订单
     *
     * @param request
     * @return
     */
    public OrderResponse timeout(OrderTimeoutRequest request) {
        return doExecute(request, tradeOrder -> tradeOrder.close(request));
    }

    private TradeOrder doCreate(OrderCreateRequest request) {
        // 创建订单
        TradeOrder tradeOrder = TradeOrder.createOrder(request);

        // 将订单保存到数据库
        boolean result = save(tradeOrder);
        Assert.isTrue(result, () -> new BizException(RepoErrorCode.INSERT_FAILED));

        // 写入订单流水
        TradeOrderStream orderStream = new TradeOrderStream(tradeOrder, request.getOrderEvent(), request.getIdentifier());
        result = (orderStreamMapper.insert(orderStream) == 1);
        Assert.isTrue(result, () -> new BizException(RepoErrorCode.INSERT_FAILED));

        return tradeOrder;
    }

    /**
     * 通用的订单更新方法
     *
     * @param orderRequest
     * @param consumer
     * @return
     */
    private OrderResponse doExecute(BaseOrderUpdateRequest orderRequest, Consumer<TradeOrder> consumer) {
        OrderResponse orderResponse = new OrderResponse();
        return handle(orderRequest, orderResponse, "doExecute", request -> {
            // 校验订单是否存在
            TradeOrder existOrder = orderMapper.selectByOrderId(orderRequest.getOrderId());
            if (existOrder == null) {
                throw new OrderException(ORDER_NOT_EXIST);
            }

            // 校验操作者是否有权限执行这次操作
            if (!hasPermission(existOrder, orderRequest.getOrderEvent(), orderRequest.getOperator(), orderRequest.getOperatorType())) {
                throw new OrderException(PERMISSION_DENIED);
            }

            // 进行幂等校验, 判断是否已经存在对应的流水了
            TradeOrderStream existStream = orderStreamMapper.selectByIdentifier(orderRequest.getIdentifier(), orderRequest.getOrderEvent().name(), orderRequest.getOrderId());
            if (existStream != null) {
                return new OrderResponse.OrderResponseBuilder().orderId(existStream.getOrderId()).streamId(existStream.getId().toString()).buildDuplicated();
            }

            //核心逻辑执行
            consumer.accept(existOrder);

            return transactionTemplate.execute(transactionStatus -> {
                // 更新修改后的订单
                boolean result = (orderMapper.updateByOrderId(existOrder) == 1);
                Assert.isTrue(result, () -> new OrderException(OrderErrorCode.UPDATE_ORDER_FAILED));

                // 写入流水
                TradeOrderStream orderStream = new TradeOrderStream(existOrder, orderRequest.getOrderEvent(), orderRequest.getIdentifier());
                result = orderStreamMapper.insert(orderStream) == 1;
                Assert.isTrue(result, () -> new BizException(RepoErrorCode.INSERT_FAILED));

                return new OrderResponse.OrderResponseBuilder()
                        .orderId(orderStream.getOrderId()).streamId(String.valueOf(orderStream.getId())).buildSuccess();
            });
        });
    }

    private static <T, R extends OrderResponse> OrderResponse handle(T request, R response, String method, Function<T, R> function) {
        logger.info("before execute method={}, request={}", method, JSON.toJSONString(request));

        try {
            // 校验 request 是否为 null
            requireNonNull(request);
            // 校验 request 是否所有字段都满足对应注解的规则
            BeanValidator.validateObject(request);

            // 执行方法
            response = function.apply(request);
        } catch (OrderException e) {
            logger.error(e.toString(), e);

            response.setSuccess(false);
            response.setResponseCode(e.getErrorCode().getCode());
            response.setResponseMessage(e.getErrorCode().getMessage());
            logger.error("failed execute method={}, exception={}", method, JSON.toJSONString(e));
        } catch (Exception e) {
            response.setSuccess(false);
            response.setResponseCode(SYSTEM_ERROR.name());
            response.setResponseMessage(e.getMessage());
            logger.error("failed execute method={}, exception={}", method, JSON.toJSONString(e));
        } finally {
            logger.info("after execute method={}, result={}", method, JSON.toJSONString(response));
        }

        return response;
    }

    private boolean hasPermission(TradeOrder existOrder, TradeOrderEvent orderEvent, String operator, UserType operatorType) {
        switch (orderEvent) {
            // 必须是订单持有者本人进行操作
            case PAY:
            case CANCEL:
            case CREATE_AND_CONFIRM:
                return existOrder.getBuyerId().equals(operator);
            // 必须是平台进行操作
            case TIME_OUT:
            case CONFIRM:
            case FINISH:
            case DISCARD:
                return operatorType == UserType.PLATFORM;
            default:
                throw new UnsupportedOperationException("unsupport order event : " + orderEvent);
        }
    }
}
