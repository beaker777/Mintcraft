package com.beaker.mintcraft.inventory.facade;

import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.response.InventoryResponse;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.inventory.domain.service.impl.CollectionInventoryService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author beaker
 * @Date 2026/5/10 21:08
 * @Description 库存 facade 层接口实现类
 */
@DubboService
public class InventoryFacadeServiceImpl implements InventoryFacadeService {

    @Resource
    private CollectionInventoryService collectionInventoryService;

    private static final String ERROR_CODE_UNSUPPORTED_GOODS_TYPE = "UNSUPPORTED_GOODS_TYPE";


    @Override
    public SingleResponse<Boolean> init(InventoryRequest inventoryRequest) {
        GoodsType goodsType = inventoryRequest.getGoodsType();

        // TODO : 后续补充盲盒库存查询
        InventoryResponse inventoryResponse = switch (goodsType) {
            case COLLECTION -> collectionInventoryService.init(inventoryRequest);
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };

        if (inventoryResponse.getSuccess()) {
            return SingleResponse.of(true);
        }
        return SingleResponse.fail(inventoryResponse.getResponseCode(), inventoryResponse.getResponseMessage());
    }

    @Override
    public SingleResponse<Integer> queryInventory(InventoryRequest inventoryRequest) {
        GoodsType goodsType = inventoryRequest.getGoodsType();

        // TODO: 这里要添加一个是否售罄的缓存池

        Integer inventory = switch (goodsType) {
            case COLLECTION -> collectionInventoryService.getInventory(inventoryRequest);
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };

        return SingleResponse.of(inventory);
    }

    @Override
    public SingleResponse<Boolean> decrease(InventoryRequest inventoryRequest) {
        GoodsType goodsType = inventoryRequest.getGoodsType();

        // TODO: 这里要添加一个是否售罄的缓存池

        InventoryResponse inventoryResponse = switch (goodsType) {
            case COLLECTION -> collectionInventoryService.decrease(inventoryRequest);
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };

        if (!inventoryResponse.getSuccess()) {
            return SingleResponse.fail(inventoryResponse.getResponseCode(), inventoryResponse.getResponseMessage());
        }
        return SingleResponse.of(true);
    }

}
