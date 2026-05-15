package com.beaker.mintcraft.order.facade;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.api.order.request.OrderPageQueryRequest;
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
import com.beaker.mintcraft.order.domain.entity.TradeOrder;
import com.beaker.mintcraft.order.domain.entity.convertor.TradeOrderConvertor;
import com.beaker.mintcraft.order.domain.service.OrderManageService;
import com.beaker.mintcraft.order.domain.service.OrderService;
import com.beaker.mintcraft.order.exception.OrderException;
import com.beaker.mintcraft.order.validator.OrderCreateValidator;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.INVENTORY_DECREASE_FAILED;
import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.ORDER_CREATE_VALID_FAILED;

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
    private OrderCreateValidator orderValidatorChain;

    @Resource
    private UserFacadeService userFacadeService;

    @Resource
    private InventoryFacadeService inventoryFacadeService;

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
