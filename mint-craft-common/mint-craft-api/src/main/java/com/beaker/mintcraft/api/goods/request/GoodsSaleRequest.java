package com.beaker.mintcraft.api.goods.request;

import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author beaker
 * @Date 2026/5/15 20:07
 * @Description 商品销售请求
 */
@Data
public class GoodsSaleRequest extends BaseGoodsRequest {

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
}
