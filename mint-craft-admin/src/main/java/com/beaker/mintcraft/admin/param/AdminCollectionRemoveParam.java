package com.beaker.mintcraft.admin.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/6/1 20:29
 * @Description 藏品下架参数
 */
@Data
public class AdminCollectionRemoveParam {

    /**
     * '藏品id'
     */
    @NotNull(message = "藏品id不能为空")
    private Long collectionId;
}
