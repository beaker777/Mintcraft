package com.beaker.mintcraft.collection.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/6/4 20:07
 * @Description 交换参数
 */
@Data
public class TransferParam {

    @NotNull(message = "heldCollectionId is null")
    private String heldCollectionId;

    @NotNull(message = "recipientUserId is null")
    private String recipientUserId;

}
