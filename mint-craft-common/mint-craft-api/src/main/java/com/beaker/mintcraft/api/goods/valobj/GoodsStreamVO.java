package com.beaker.mintcraft.api.goods.valobj;

import com.beaker.mintcraft.api.box.constant.BlindBoxState;
import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author beaker
 * @Date 2026/5/17 21:32
 * @Description 商品流水对象
 */
@Data
public class GoodsStreamVO implements Serializable {

    /**
     * 流水类型
     */
    private GoodsEvent streamType;

    /**
     * '幂等号'
     */
    private String identifier;

    /**
     * '变更数量'
     */
    private Integer changedQuantity;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品类型
     */
    private GoodsType goodsType;

    /**
     * '价格'
     */
    private BigDecimal price;

    /**
     * '数量'
     */
    private Long quantity;

    /**
     * '可售库存'
     */
    private Long saleableInventory;

    /**
     * '冻结库存'
     */
    private Long frozenInventory;

    /**
     * '状态'
     */
    private BlindBoxState state;

    /**
     * 扩展信息
     */
    private String extendInfo;
}
