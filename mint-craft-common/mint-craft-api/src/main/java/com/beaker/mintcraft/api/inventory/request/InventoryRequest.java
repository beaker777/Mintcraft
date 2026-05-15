package com.beaker.mintcraft.api.inventory.request;

import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/5/10 21:05
 * @Description 库存请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest extends BaseRequest {

    /**
     * 商品ID
     */
    @NotNull(message = "goods is null")
    private String goodsId;

    /**
     * 商品ID
     */
    @NotNull(message = "goodsType is null")
    private GoodsType goodsType;

    /**
     * 唯一标识
     */
    private String identifier;

    /**
     * 库存数量
     */
    private Integer inventory;

    public InventoryRequest(OrderCreateRequest orderCreateRequest) {
        this.goodsId = orderCreateRequest.getGoodsId();
        this.goodsType = orderCreateRequest.getGoodsType();
        this.identifier = orderCreateRequest.getOrderId();
        this.inventory = orderCreateRequest.getItemCount();
    }
}
