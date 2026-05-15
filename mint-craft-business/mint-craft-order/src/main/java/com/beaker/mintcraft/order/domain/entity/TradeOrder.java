package com.beaker.mintcraft.order.domain.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.order.constant.TradeOrderEvent;
import com.beaker.mintcraft.api.order.constant.TradeOrderState;
import com.beaker.mintcraft.api.order.request.OrderConfirmRequest;
import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.api.pay.constant.PayChannel;
import com.beaker.mintcraft.api.user.constant.UserType;
import com.beaker.mintcraft.datasource.domain.entity.BaseEntity;
import com.beaker.mintcraft.order.domain.entity.convertor.TradeOrderConvertor;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/5/12 20:42
 * @Description 订单
 */
@Data
@TableName("trade_order")
public class TradeOrder extends BaseEntity {

    /**
     * 默认超时时间
     */
    public static final int DEFAULT_TIME_OUT_MINUTES = 30;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 买家id
     */
    private String buyerId;

    /**
     * 买家 ID 的逆序
     */
    private String reverseBuyerId;

    /**
     * 买家id类型
     */
    private UserType buyerType;

    /**
     * 卖家id
     */
    private String sellerId;

    /**
     * 卖家id类型
     */
    private UserType sellerType;

    /**
     * 幂等号
     */
    private String identifier;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 商品数量
     */
    private int itemCount;

    /**
     * 商品单价
     */
    private BigDecimal itemPrice;

    /**
     * 已支付金额
     */
    private BigDecimal paidAmount;

    /**
     * 支付成功时间
     */
    private Date paySucceedTime;

    /**
     * 下单确认时间
     */
    private Date orderConfirmedTime;

    /**
     * 下单完成时间
     */
    private Date orderFinishedTime;

    /**
     * 订单关闭时间
     */
    private Date orderClosedTime;

    /**
     * 商品Id
     */
    private String goodsId;

    /**
     * 商品类型
     */
    private GoodsType goodsType;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品图片
     */
    private String goodsPicUrl;

    /**
     * 支付方式
     */
    private PayChannel payChannel;

    /**
     * 支付流水号
     */
    private String payStreamId;

    /**
     * 订单状态
     */
    private TradeOrderState orderState;

    /**
     * 关单类型
     */
    private String closeType;

    /**
     * 快照版本
     */
    private Integer snapshotVersion;

    @JSONField(serialize = false)
    public Boolean isTimeout() {
        //订单已关闭 (订单未支付且未关闭 并且 订单已经达到了超时时间)
        return (orderState == TradeOrderState.CLOSED && closeType == TradeOrderEvent.TIME_OUT.name())
                || (orderState == TradeOrderState.CONFIRM && this.getGmtCreate().compareTo(DateUtils.addMinutes(new Date(), -TradeOrder.DEFAULT_TIME_OUT_MINUTES)) < 0);
    }

    @JSONField(serialize = false)
    public Boolean isClosed() {
        return orderState == TradeOrderState.CLOSED;
    }

    @JSONField(serialize = false)
    public Date getPayExpireTime() {
        return DateUtils.addMinutes(this.getGmtCreate(), TradeOrder.DEFAULT_TIME_OUT_MINUTES);
    }

    public static TradeOrder createOrder(OrderCreateRequest request) {
        TradeOrder tradeOrder = TradeOrderConvertor.INSTANCE.mapToEntity(request);
        tradeOrder.setReverseBuyerId(StringUtils.reverse(request.getBuyerId()));
        tradeOrder.setOrderState(TradeOrderState.CREATE);
        tradeOrder.setPaidAmount(BigDecimal.ZERO);
        tradeOrder.setOrderId(request.getOrderId());

        return tradeOrder;
    }

    public TradeOrder confirm(OrderConfirmRequest request) {
        this.setOrderConfirmedTime(request.getOperateTime());
        // TODO: 这里后期要补充上订单状态机
        this.setOrderState(TradeOrderState.CONFIRM);

        return this;
    }

}
