package com.beaker.mintcraft.order.validator.impl;

import com.beaker.mintcraft.api.collection.service.CollectionFacadeService;
import com.beaker.mintcraft.api.collection.valobj.CollectionVO;
import com.beaker.mintcraft.api.goods.constant.GoodsState;
import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.goods.valobj.BaseGoodsVO;
import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.order.exception.OrderException;

import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.GOODS_NOT_AVAILABLE;
import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.GOODS_PRICE_CHANGED;

/**
 * @Author beaker
 * @Date 2026/5/14 16:21
 * @Description 商品校验
 */
public class GoodsValidator extends BaseOrderCreateValidator {

    private GoodsFacadeService goodsFacadeService;

    public GoodsValidator(GoodsFacadeService goodsFacadeService) {
        this.goodsFacadeService = goodsFacadeService;
    }

    public GoodsValidator() {

    }

    @Override
    protected void doValidate(OrderCreateRequest request) throws OrderException {
        BaseGoodsVO baseGoodsVO = goodsFacadeService.getGoods(request.getGoodsId(), request.getGoodsType());

        // 如果商品不是可售状态, 则返回失败
        if (baseGoodsVO.getState() != GoodsState.SELLING && baseGoodsVO.getState() != GoodsState.SOLD_OUT) {
            throw new OrderException(GOODS_NOT_AVAILABLE);
        }
        // 校验商品的售价是否被修改了
        if (baseGoodsVO.getPrice().compareTo(request.getItemPrice()) != 0) {
            throw new OrderException(GOODS_PRICE_CHANGED);
        }
    }
}
