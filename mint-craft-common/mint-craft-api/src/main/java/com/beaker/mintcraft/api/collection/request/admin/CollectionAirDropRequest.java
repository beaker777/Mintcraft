package com.beaker.mintcraft.api.collection.request.admin;

import com.beaker.mintcraft.api.collection.constant.GoodsSaleBizType;
import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/6/5 20:05
 * @Description 藏品空投请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionAirDropRequest extends BaseCollectionRequest {

    /**
     * 接收用户ID
     */
    @NotNull(message = "recipientUserId 不能为空")
    private String recipientUserId;

    /**
     * 数量
     */
    @Min(value = 1, message = "数量不能小于1")
    private Integer quantity;

    /**
     * 商品类型
     */
    @NotNull(message = "bizType 不能为空")
    private GoodsSaleBizType bizType;

    @Override
    public GoodsEvent getEventType() {
        return GoodsEvent.AIR_DROP;
    }
}
