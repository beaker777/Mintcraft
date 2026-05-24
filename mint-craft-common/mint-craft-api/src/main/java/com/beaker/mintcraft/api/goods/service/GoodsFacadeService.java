package com.beaker.mintcraft.api.goods.service;

import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.goods.request.GoodsSaleRequest;
import com.beaker.mintcraft.api.goods.response.GoodsSaleResponse;
import com.beaker.mintcraft.api.goods.valobj.BaseGoodsVO;
import com.beaker.mintcraft.api.goods.valobj.GoodsStreamVO;

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
     * 藏品出售的 try 阶段，做库存占用
     *
     * @param request
     * @return
     */
    public GoodsSaleResponse sale(GoodsSaleRequest request);

    /**
     * 藏品出售的 cancel 阶段，做库存退还
     *
     * @param request
     * @return
     */
    public GoodsSaleResponse cancelSale(GoodsSaleRequest request);

    /**
     * 获取商品流水
     *
     * @param goodsId
     * @param goodsType
     * @param goodsEvent
     * @param identifier
     * @return
     */
    public GoodsStreamVO getGoodsInventoryStream(String goodsId, GoodsType goodsType, GoodsEvent goodsEvent, String identifier);

    /**
     * 支付成功
     *
     * @param request
     * @return
     */
    GoodsSaleResponse paySuccess(GoodsSaleRequest request);
}
