package com.beaker.mintcraft.api.goods.request;

import com.beaker.mintcraft.api.goods.constant.GoodsEvent;

/**
 * @Author beaker
 * @Date 2026/5/21 22:41
 * @Description 商品取消销售请求
 */
public record GoodsCancelSaleRequest(String identifier, Long collectionId, Integer quantity) {

    public GoodsEvent eventType() {
        return GoodsEvent.CANCEL_SALE;
    }
}
