package com.beaker.mintcraft.api.collection.request.admin;

import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/6/1 20:31
 * @Description 藏品下架请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionRemoveRequest extends BaseCollectionRequest {

    @Override
    public GoodsEvent getEventType() {
        return GoodsEvent.REMOVE;
    }
}
