package com.beaker.mintcraft.api.goods.request;

import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import com.beaker.mintcraft.api.order.request.OrderCreateAndConfirmRequest;
import com.beaker.mintcraft.api.order.valobj.TradeOrderVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author beaker
 * @Date 2026/5/15 20:07
 * @Description 商品销售请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsSaleRequest extends BaseGoodsRequest implements Serializable {

    /**
     * 藏品名称
     */
    private String name;

    /**
     * 藏品封面
     */
    private String cover;

    /**
     * 购入价格
     */
    private BigDecimal purchasePrice;

    /**
     * '持有人id'
     */
    private String userId;

    /**
     * 销售数量
     */
    private Integer quantity;

    /**
     * 业务单号
     */
    private String bizNo;

    /**
     * 业务类型
     *
     */
    private String bizType;


    @Override
    public GoodsEvent getEventType() {
        return GoodsEvent.SALE;
    }

    public GoodsSaleRequest(OrderCreateAndConfirmRequest orderCreateAndConfirmRequest) {
        this.userId = orderCreateAndConfirmRequest.getBuyerId();
        this.quantity = orderCreateAndConfirmRequest.getItemCount();
        this.bizNo = orderCreateAndConfirmRequest.getOrderId();
        super.setGoodsId(Long.valueOf(orderCreateAndConfirmRequest.getGoodsId()));
        super.setGoodsType(orderCreateAndConfirmRequest.getGoodsType().name());
        super.setIdentifier(orderCreateAndConfirmRequest.getOrderId());
    }

    public GoodsSaleRequest(TradeOrderVO tradeOrderVO) {
        this.setBizNo(tradeOrderVO.getOrderId());
        this.setIdentifier(tradeOrderVO.getOrderId());
        this.setQuantity(tradeOrderVO.getItemCount());
        this.setGoodsType(tradeOrderVO.getGoodsType().name());
        this.setGoodsId(Long.valueOf(tradeOrderVO.getGoodsId()));
    }

}
