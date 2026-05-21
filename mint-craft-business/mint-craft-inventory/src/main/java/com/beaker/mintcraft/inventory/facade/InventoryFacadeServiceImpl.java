package com.beaker.mintcraft.inventory.facade;

import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.goods.service.GoodsFacadeService;
import com.beaker.mintcraft.api.goods.valobj.GoodsStreamVO;
import com.beaker.mintcraft.api.inventory.request.InventoryCheckRequest;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.response.InventoryCheckResponse;
import com.beaker.mintcraft.api.inventory.response.InventoryResponse;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.inventory.domain.service.impl.CollectionInventoryService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.beaker.mintcraft.api.common.constant.CommonConstant.SEPARATOR;
import static com.beaker.mintcraft.inventory.domain.service.impl.AbstractInventoryServiceImpl.ERROR_CODE_INVENTORY_IS_ZERO;
import static com.beaker.mintcraft.inventory.domain.service.impl.AbstractInventoryServiceImpl.ERROR_CODE_INVENTORY_NOT_ENOUGH;

/**
 * @Author beaker
 * @Date 2026/5/10 21:08
 * @Description 库存 facade 层接口实现类
 */
@Slf4j
@DubboService
public class InventoryFacadeServiceImpl implements InventoryFacadeService {

    @Resource
    private CollectionInventoryService collectionInventoryService;

    @DubboReference
    private GoodsFacadeService goodsFacadeService;

    private Cache<String, Boolean> soldOutGoodsLocalCache;

    private static final String ERROR_CODE_UNSUPPORTED_GOODS_TYPE = "UNSUPPORTED_GOODS_TYPE";

    @PostConstruct
    public void init() {
        soldOutGoodsLocalCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(3000)
                .build();
    }

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

        // 如果商品在缓存池中直接返回
        if (soldOutGoodsLocalCache.getIfPresent(goodsType + SEPARATOR + inventoryRequest.getGoodsId()) != null) {
            return SingleResponse.of(0);
        }

        Integer inventory = switch (goodsType) {
            case COLLECTION -> collectionInventoryService.getInventory(inventoryRequest);
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };

        return SingleResponse.of(inventory);
    }

    @Override
    public SingleResponse<Boolean> decrease(InventoryRequest inventoryRequest) {
        GoodsType goodsType = inventoryRequest.getGoodsType();

        // 如果商品在缓存池中说明库存不足
        if (soldOutGoodsLocalCache.getIfPresent(goodsType + SEPARATOR + inventoryRequest.getGoodsId()) != null) {
            return SingleResponse.fail(ERROR_CODE_INVENTORY_NOT_ENOUGH, "库存不足");
        }

        InventoryResponse inventoryResponse = switch (goodsType) {
            case COLLECTION -> collectionInventoryService.decrease(inventoryRequest);
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };

        // 若扣减成功, 且扣减后库存为 0, 加入缓存
        // 若由于库存已经为 0 扣减失败, 加入缓存
        if (isSoldOut(inventoryResponse)) {
            soldOutGoodsLocalCache.put(goodsType + SEPARATOR + inventoryRequest.getGoodsId(), true);
        }

        if (!inventoryResponse.getSuccess()) {
            return SingleResponse.fail(inventoryResponse.getResponseCode(), inventoryResponse.getResponseMessage());
        }
        return SingleResponse.of(true);
    }

    @Override
    public SingleResponse<Boolean> increase(InventoryRequest inventoryRequest) {
        GoodsType goodsType = inventoryRequest.getGoodsType();

        InventoryResponse inventoryResponse = switch (goodsType) {
            case COLLECTION ->  collectionInventoryService.increase(inventoryRequest);
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };

        if (inventoryResponse.getSuccess()) {
            // 如果已售罄商品增加库存, 从缓存池中移除
            // fixme: 这里缓存池是本地缓存, 所以无法保证一致性, 但我们设置了 1min 的过期时间, 1min 的延迟是允许的
            if (inventoryResponse.getInventory() != null && inventoryResponse.getInventory() > 0) {
                soldOutGoodsLocalCache.invalidate(goodsType + SEPARATOR + inventoryRequest.getGoodsId());
            }

            return SingleResponse.of(true);
        }

        return SingleResponse.fail(inventoryResponse.getResponseCode(), inventoryResponse.getResponseMessage());
    }

    @Override
    public SingleResponse<Long> removeInventoryDecreaseLog(InventoryRequest inventoryRequest) {
        GoodsType goodsType = inventoryRequest.getGoodsType();

        Long inventoryResponse = switch (goodsType) {
            case COLLECTION -> collectionInventoryService.removeInventoryDecreaseLog(inventoryRequest);
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };

        return SingleResponse.of(inventoryResponse);
    }

    @Override
    public SingleResponse<String> getInventoryDecreaseLog(InventoryRequest inventoryRequest) {
        GoodsType goodsType = inventoryRequest.getGoodsType();

        String inventoryResponse = switch (goodsType) {
            case COLLECTION -> collectionInventoryService.getInventoryDecreaseLog(inventoryRequest);
            default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
        };

        return SingleResponse.of(inventoryResponse);
    }

    @Override
    public SingleResponse<String> getInventoryIncreaseLog(InventoryRequest inventoryRequest) {
        GoodsType goodsType = inventoryRequest.getGoodsType();

        String inventoryResponse = switch (goodsType) {
            case COLLECTION -> collectionInventoryService
        }
    }

    @Override
    public InventoryCheckResponse check(InventoryCheckRequest request) {
        InventoryCheckResponse inventoryCheckResponse = new InventoryCheckResponse();

        boolean checkResult;
        GoodsStreamVO goodsStreamVO = goodsFacadeService.getGoodsInventoryStream(request.getGoodsId(), request.getGoodsType(), request.getGoodsEvent(), request.getIdentifier());
        // 判断扣减库存流水是否存在, 若存在则判断流水扣减库存与请求扣减库存是否一致
        if (goodsStreamVO == null) {
            checkResult = false;
        } else {
            checkResult = goodsStreamVO.getChangedQuantity().equals(request.getChangedQuantity());
        }

        inventoryCheckResponse.setSuccess(true);
        inventoryCheckResponse.setCheckResult(checkResult);
        return inventoryCheckResponse;
    }

    private static boolean isSoldOut(InventoryResponse inventoryResponse) {
        if (inventoryResponse.getSuccess() && inventoryResponse.getInventory() == 0) {
            //这部分代码没有实际功能作用，仅用于日志埋点，方便压测时判断延时，详见压测相关视频
            log.warn("debug:soldOut ...");
        }
        return inventoryResponse.getSuccess() && inventoryResponse.getInventory() == 0
                || !inventoryResponse.getSuccess() && inventoryResponse.getResponseCode().equals(ERROR_CODE_INVENTORY_IS_ZERO);
    }

}
