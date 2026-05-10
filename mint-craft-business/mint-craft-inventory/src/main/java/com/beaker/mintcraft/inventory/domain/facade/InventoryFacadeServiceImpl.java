package com.beaker.mintcraft.inventory.domain.facade;

import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.inventory.domain.domain.service.impl.CollectionInventoryService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import static com.beaker.mintcraft.api.goods.constant.GoodsType.COLLECTION;

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
    public SingleResponse<Integer> queryInventory(InventoryRequest inventoryRequest) {
        GoodsType goodsType = inventoryRequest.getGoodsType();

        // TODO: 这里要添加一个是否售罄的缓存池

        Integer inventory = switch (goodsType) {
            case COLLECTION -> collectionInventoryService.getInventory(inventoryRequest);
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };

        return SingleResponse.of(inventory);
    }
}
