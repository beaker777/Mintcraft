package com.beaker.mintcraft.api.collection.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author beaker
 * @Date 2026/5/26 18:27
 * @Description 持有藏品 DTO
 */
@Data
public class HeldCollectionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 Id
     */
    private Long id;

    /**
     * '藏品id'
     */
    private Long collectionId;

    /**
     * '持有人id'
     */
    private String userId;

    /**
     * '状态'
     */
    private String state;

    /**
     * 业务单号
     */
    private String bizNo;

    /**
     * 业务类型
     */
    private String bizType;
}
