package com.beaker.mintcraft.collection.domain.entity;

import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import com.beaker.mintcraft.datasource.domain.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/6/5 20:28
 * @Description 空投流水
 */
@Data
@NoArgsConstructor
public class CollectionAirdropStream extends BaseEntity {

    /**
     * 藏品id
     */
    private Long collectionId;

    /**
     * '接收用户ID'
     */
    private String recipientUserId;

    /**
     * '空投数量'
     */
    private Integer quantity;

    /**
     * 流水类型
     */
    private GoodsEvent streamType;

    /**
     * '幂等号'
     */
    private String identifier;

    public CollectionAirdropStream(Collection collection, String identifier, GoodsEvent streamType, Integer quantity, String recipientUserId) {
        this.collectionId = collection.getId();
        this.quantity = quantity;
        this.streamType = streamType;
        this.identifier = identifier;
        this.recipientUserId = recipientUserId;

        super.setLockVersion(collection.getLockVersion());
        super.setDeleted(collection.getDeleted());
    }
}
