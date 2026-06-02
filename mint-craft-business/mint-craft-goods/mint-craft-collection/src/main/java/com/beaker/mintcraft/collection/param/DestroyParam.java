package com.beaker.mintcraft.collection.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/6/2 21:38
 * @Description 销毁参数
 */
@Data
public class DestroyParam {

    @NotNull(message = "heldCollectionId is null")
    private String heldCollectionId;
}
