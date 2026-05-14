package com.beaker.mintcraft.order.validator.impl;

import com.beaker.mintcraft.api.collection.service.CollectionFacadeService;
import com.beaker.mintcraft.api.collection.valobj.CollectionVO;
import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.order.exception.OrderException;

/**
 * @Author beaker
 * @Date 2026/5/14 16:21
 * @Description 商品校验
 */
public class GoodsValidator extends BaseOrderCreateValidator {

    @Override
    protected void doValidate(OrderCreateRequest request) throws OrderException {
        // TODO : 完善 goods 模块对外接口
    }
}
