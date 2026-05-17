package com.beaker.mintcraft.api.inventory.response;

import com.beaker.mintcraft.base.response.BaseResponse;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/17 21:24
 * @Description 库存校验响应类
 */
@Data
public class InventoryCheckResponse extends BaseResponse {

    /**
     * 核对结果
     */
    private Boolean checkResult;
}
