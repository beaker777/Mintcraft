package com.beaker.mintcraft.admin.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/6/5 20:01
 * @Description 空投参数
 */
@Data
public class AdminCollectionAirDropParam {

    /**
     * '藏品id'
     */
    @NotNull(message = "藏品id不能为空")
    private Long collectionId;


    /**
     * '接收用户id'
     */
    @NotNull(message = "接收用户id不能为空")
    private String recipientUserId;

    /**
     * '藏品数量'
     */
    private Integer quantity;


    /**
     * '藏品交易类型'
     */
    private String bizType;
}
