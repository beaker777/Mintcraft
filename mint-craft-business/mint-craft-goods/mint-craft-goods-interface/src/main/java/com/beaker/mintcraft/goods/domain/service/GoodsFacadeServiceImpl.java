package com.beaker.mintcraft.goods.domain.service;

import com.beaker.mintcraft.api.collection.service.CollectionFacadeService;
import com.beaker.mintcraft.api.collection.valobj.CollectionVO;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.goods.valobj.BaseGoodsVO;
import com.beaker.mintcraft.base.response.SingleResponse;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author beaker
 * @Date 2026/5/15 13:14
 * @Description 商品 facade 层实现类
 */
@DubboService
public class GoodsFacadeServiceImpl implements GoodsFacadeService {

    @Resource
    private CollectionFacadeService collectionFacadeService;

    private static final String ERROR_CODE_UNSUPPORTED_GOODS_TYPE = "UNSUPPORTED_GOODS_TYPE";


    @Override
    public BaseGoodsVO getGoods(String goodsId, GoodsType goodsType) {
        return switch (goodsType) {
            case COLLECTION -> {
                SingleResponse<CollectionVO> response = collectionFacadeService.queryById(Long.valueOf(goodsId));
                if (response.getSuccess()) {
                    yield response.getData();
                }
                yield null;
            }
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };
    }
}
