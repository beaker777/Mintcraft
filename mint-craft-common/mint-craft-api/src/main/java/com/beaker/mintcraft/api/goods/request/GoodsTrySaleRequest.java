package com.beaker.mintcraft.api.goods.request;

import com.beaker.mintcraft.api.goods.constant.GoodsEvent;

/**
 * @Author beaker
 * @Date 2026/5/16 13:33
 * @Description 商品尝试销售请求
 */
public record GoodsTrySaleRequest(String identifier, Long goodsId, Integer quantity) {

    public GoodsEvent eventType() {
        return GoodsEvent.TRY_SALE;
    }
}
