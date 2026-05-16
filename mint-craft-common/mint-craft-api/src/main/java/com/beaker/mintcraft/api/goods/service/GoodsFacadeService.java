package com.beaker.mintcraft.api.goods.service;

import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.goods.request.GoodsSaleRequest;
import com.beaker.mintcraft.api.goods.response.GoodsSaleResponse;
import com.beaker.mintcraft.api.goods.valobj.BaseGoodsVO;

/**
 * @Author beaker
 * @Date 2026/5/15 13:11
 * @Description 商品 facade 层接口
 */
public interface GoodsFacadeService {

    /**
     * 获取商品
     *
     * @param goodsId
     * @param goodsType
     * @return
     */
    public BaseGoodsVO getGoods(String goodsId, GoodsType goodsType);

    /**
     * 藏品出售的try阶段，做库存占用
     *
     * @param request
     * @return
     */
    public GoodsSaleResponse sale(GoodsSaleRequest request);
}
