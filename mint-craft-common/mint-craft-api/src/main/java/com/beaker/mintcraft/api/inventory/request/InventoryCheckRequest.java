package com.beaker.mintcraft.api.inventory.request;

import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/16 21:14
 * @Description 库存校验请求
 */
@Data
public class InventoryCheckRequest extends BaseRequest {

    /**
     * '商品ID
     */
    @NotNull(message = "goodsId不能为空")
    private String goodsId;

    /**
     * '商品类型'
     */
    @NotNull(message = "goodsType不能为空")
    private GoodsType goodsType;

    /**
     * '标识符'
     */
    @NotNull(message = "identifier不能为空")
    private String identifier;

    /**
     * '变更数量'
     */
    @NotNull(message = "changedQuantity不能为空")
    private Integer changedQuantity;

    /**
     * '商品事件'
     */
    @NotNull(message = "goodsEvent不能为空")
    private GoodsEvent goodsEvent;
}
