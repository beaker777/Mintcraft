package com.beaker.mintcraft.goods.facade;

import com.beaker.mintcraft.api.collection.service.CollectionFacadeService;
import com.beaker.mintcraft.api.collection.valobj.CollectionVO;
import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.goods.request.GoodsCancelSaleRequest;
import com.beaker.mintcraft.api.goods.request.GoodsSaleRequest;
import com.beaker.mintcraft.api.goods.request.GoodsTrySaleRequest;
import com.beaker.mintcraft.api.goods.response.GoodsSaleResponse;
import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.goods.valobj.BaseGoodsVO;
import com.beaker.mintcraft.api.goods.valobj.GoodsStreamVO;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.collection.domain.entity.CollectionInventoryStream;
import com.beaker.mintcraft.collection.domain.service.CollectionService;
import com.beaker.mintcraft.collection.infrastructure.mapper.CollectionInventoryStreamMapper;
import com.beaker.mintcraft.goods.domain.entity.convertor.GoodsStreamConvertor;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author beaker
 * @Date 2026/5/15 13:14
 * @Description 商品 facade 层实现类
 */
@DubboService
public class GoodsFacadeServiceImpl implements GoodsFacadeService {

    @Resource
    private CollectionService collectionService;

    @Resource
    private CollectionInventoryStreamMapper collectionInventoryStreamMapper;

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

    @Override
    public GoodsSaleResponse sale(GoodsSaleRequest request) {
        GoodsSaleResponse response = new GoodsSaleResponse();

        GoodsTrySaleRequest goodsTrySaleRequest = new
                GoodsTrySaleRequest(request.getIdentifier(), request.getGoodsId(), request.getQuantity());
        GoodsType goodsType = GoodsType.valueOf(request.getGoodsType());

        Boolean trySaleResult =  switch (goodsType) {
            case COLLECTION -> collectionService.sale(goodsTrySaleRequest);
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };

        response.setSuccess(trySaleResult);
        return response;
    }

    @Override
    public GoodsSaleResponse cancelSale(GoodsSaleRequest request) {
        GoodsSaleResponse goodsSaleResponse = new GoodsSaleResponse();
        GoodsCancelSaleRequest goodsCancelSaleRequest = new GoodsCancelSaleRequest(request.getIdentifier(), request.getGoodsId(), request.getQuantity());

        GoodsType goodsType = GoodsType.valueOf(request.getGoodsType());

        Boolean result = switch (goodsType) {
            case COLLECTION -> collectionService.cancel(goodsCancelSaleRequest);
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };

        goodsSaleResponse.setSuccess(result);
        return goodsSaleResponse;
    }

    @Override
    public GoodsStreamVO getGoodsInventoryStream(String goodsId, GoodsType goodsType, GoodsEvent goodsEvent, String identifier) {
        return switch (goodsType) {
            case COLLECTION -> {
                CollectionInventoryStream collectionInventoryStream = collectionInventoryStreamMapper
                        .selectByIdentifier(identifier, goodsEvent.name(), Long.valueOf(goodsId));

                yield GoodsStreamConvertor.INSTANCE.mapToVo(collectionInventoryStream);
            }
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };
    }
}
