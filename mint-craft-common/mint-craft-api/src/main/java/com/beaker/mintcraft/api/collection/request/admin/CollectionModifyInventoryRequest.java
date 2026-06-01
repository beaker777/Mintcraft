package com.beaker.mintcraft.api.collection.request.admin;

import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/6/1 18:30
 * @Description 藏品修改库存请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionModifyInventoryRequest extends BaseCollectionRequest {

    /**
     * '藏品数量'
     */
    private Integer quantity;


    @Override
    public GoodsEvent getEventType() {
        return GoodsEvent.MODIFY_INVENTORY;
    }
}
