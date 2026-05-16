package com.beaker.mintcraft.trade.param;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/16 18:32
 * @Description 购买参数
 */
@Data
public class BuyParam {

    @NotNull(message = "goodsId is null")
    private String goodsId;

    @NotNull(message = "goodsType is null")
    private String goodsType;

    /**
     * 商品数量
     */
    @Min(value = 1)
    private int itemCount;
}
