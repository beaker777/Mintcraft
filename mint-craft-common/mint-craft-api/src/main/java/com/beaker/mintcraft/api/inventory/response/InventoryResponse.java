package com.beaker.mintcraft.api.inventory.response;

import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.base.response.BaseResponse;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/11 16:12
 * @Description 库存响应
 */
@Data
public class InventoryResponse extends BaseResponse {


    private String goodsId;

    private GoodsType goodsType;

    private String identifier;

    private Integer inventory;
}
