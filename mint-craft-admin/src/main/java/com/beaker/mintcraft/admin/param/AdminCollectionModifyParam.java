package com.beaker.mintcraft.admin.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author beaker
 * @Date 2026/6/1 18:28
 * @Description 藏品修改参数
 */
@Data
public class AdminCollectionModifyParam {

    /**
     * '藏品id'
     */
    @NotNull(message = "藏品id不能为空")
    private Long collectionId;

    /**
     * '藏品数量'
     */
    private Integer quantity;


    /**
     * '价格'
     */
    private BigDecimal price;

}
