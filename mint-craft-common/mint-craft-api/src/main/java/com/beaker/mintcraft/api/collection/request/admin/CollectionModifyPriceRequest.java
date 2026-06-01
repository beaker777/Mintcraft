package com.beaker.mintcraft.api.collection.request.admin;

import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author beaker
 * @Date 2026/6/1 20:07
 * @Description 藏品价格修改请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionModifyPriceRequest extends BaseCollectionRequest {

    /**
     * '价格'
     */
    private BigDecimal price;


    @Override
    public GoodsEvent getEventType() {
        return GoodsEvent.MODIFY_PRICE;
    }
}
