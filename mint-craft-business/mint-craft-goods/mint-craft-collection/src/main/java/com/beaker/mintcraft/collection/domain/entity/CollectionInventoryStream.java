package com.beaker.mintcraft.collection.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.beaker.mintcraft.api.collection.constant.CollectionState;
import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import com.beaker.mintcraft.datasource.domain.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author beaker
 * @Date 2026/5/16 15:17
 * @Description 藏品库存流水
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("collection_inventory_stream")
public class CollectionInventoryStream extends BaseEntity {

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
     * 藏品id
     */
    private Long collectionId;

    /**
     * '价格'
     */
    private BigDecimal price;

    /**
     * '藏品数量'
     */
    private Integer quantity;

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
    private CollectionState state;

    /**
     * 扩展信息
     */
    private String extendInfo;

    public CollectionInventoryStream(Collection collection, String identifier, GoodsEvent streamType, Integer quantity) {
        this.collectionId = collection.getId();
        this.price = collection.getPrice();
        this.quantity = collection.getQuantity();
        this.saleableInventory = collection.getSaleableInventory();
        this.frozenInventory = collection.getFrozenInventory();
        this.state = collection.getState();

        this.streamType = streamType;
        this.identifier = identifier;
        this.changedQuantity = quantity;

        super.setLockVersion(collection.getLockVersion());
        super.setDeleted(collection.getDeleted());
    }
}
