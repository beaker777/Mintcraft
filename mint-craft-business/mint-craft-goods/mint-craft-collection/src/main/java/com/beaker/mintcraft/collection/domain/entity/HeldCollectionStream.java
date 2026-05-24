package com.beaker.mintcraft.collection.domain.entity;

import com.beaker.mintcraft.api.collection.constant.HeldCollectionEventType;
import com.beaker.mintcraft.datasource.domain.entity.BaseEntity;
import lombok.Data;

import static com.beaker.mintcraft.api.user.constant.UserType.PLATFORM;

/**
 * @Author beaker
 * @Date 2026/5/24 20:43
 * @Description 持有藏品流水
 */
@Data
public class HeldCollectionStream extends BaseEntity {

    /**
     * 持有藏品ID
     */
    private Long heldCollectionId;

    /**
     * 流水类型
     */
    private String streamType;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 幂等号
     */
    private String identifier;

    public HeldCollectionStream generateForCreate(Long heldCollectionId, String identifier) {
        this.heldCollectionId = heldCollectionId;
        this.streamType = HeldCollectionEventType.CREATE.name();
        this.operator = PLATFORM.name();
        this.identifier = identifier;

        return this;
    }
}
