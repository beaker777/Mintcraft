package com.beaker.mintcraft.collection.facade;

import com.beaker.mintcraft.api.collection.request.CollectionPageQueryRequest;
import com.beaker.mintcraft.api.collection.request.HeldCollectionPageQueryRequest;
import com.beaker.mintcraft.api.collection.service.CollectionFacadeService;
import com.beaker.mintcraft.api.collection.valobj.CollectionVO;
import com.beaker.mintcraft.api.collection.valobj.HeldCollectionVO;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.inventory.request.InventoryRequest;
import com.beaker.mintcraft.api.inventory.service.InventoryFacadeService;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.collection.domain.entity.Collection;
import com.beaker.mintcraft.collection.domain.entity.HeldCollection;
import com.beaker.mintcraft.collection.domain.entity.convertor.CollectionConvertor;
import com.beaker.mintcraft.collection.domain.entity.convertor.HeldCollectionConvertor;
import com.beaker.mintcraft.collection.domain.service.CollectionService;
import com.beaker.mintcraft.collection.domain.service.impl.HeldCollectionService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import static com.beaker.mintcraft.collection.infrastructure.exception.CollectionErrorCode.COLLECTION_NOT_EXIST;

/**
 * @Author beaker
 * @Date 2026/5/10 18:29
 * @Description 藏品 service 层实现
 */
@DubboService
public class CollectionFacadeServiceImpl implements CollectionFacadeService {

    @Resource
    private CollectionService collectionService;

    @Resource
    private HeldCollectionService heldCollectionService;

    @DubboReference
    private InventoryFacadeService inventoryFacadeService;

    @Override
    public SingleResponse<CollectionVO> queryById(Long collectionId) {
        Collection collection = collectionService.queryById(collectionId);
        if (collection == null) {
            return SingleResponse.fail(COLLECTION_NOT_EXIST.getCode(), COLLECTION_NOT_EXIST.getMessage());
        }

        // 去 redis 查询一遍最新库存
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setGoodsId(collectionId.toString());
        inventoryRequest.setGoodsType(GoodsType.COLLECTION);
        SingleResponse<Integer> response = inventoryFacadeService.queryInventory(inventoryRequest);

        // 如果 redis 没查到库存, 用数据库的库存做兜底
        Integer inventory = collection.getSaleableInventory().intValue();
        if (response.getSuccess()) {
            inventory = response.getData();
        }

        CollectionVO collectionVO = CollectionConvertor.INSTANCE.mapToVo(collection);
        collectionVO.setState(collection.getState(), collection.getSaleTime(), collection.getSaleableInventory());
        collectionVO.setInventory(inventory.longValue());

        return SingleResponse.of(collectionVO);
    }

    @Override
    public PageResponse<CollectionVO> pageQuery(CollectionPageQueryRequest request) {
        PageResponse<Collection> collections = collectionService.pageQueryByState(
                request.getKeyword(), request.getState(), request.getCurrentPage(), request.getPageSize()
        );

        // 转换成 VO 并返回结果
        return PageResponse.of(
                CollectionConvertor.INSTANCE.mapToVo(collections.getDatas()),
                collections.getTotal(), collections.getPageSize(), collections.getCurrentPage()
                );
    }

    @Override
    public SingleResponse<Long> queryHeldCollectionCount(String userId) {
        return SingleResponse.of(heldCollectionService.queryHeldCollectionCount(userId));
    }

    @Override
    public SingleResponse<HeldCollectionVO> queryHeldCollectionById(Long heldCollectionId) {
        HeldCollection heldCollection = heldCollectionService.queryById(heldCollectionId);

        return SingleResponse.of(HeldCollectionConvertor.INSTANCE.mapToVo(heldCollection));
    }

    @Override
    public PageResponse<HeldCollectionVO> pageQueryHeldCollection(HeldCollectionPageQueryRequest request) {
        PageResponse<HeldCollection> collections = heldCollectionService.pageQueryByState(request);

        return PageResponse.of(
                HeldCollectionConvertor.INSTANCE.mapToVo(collections.getDatas()),
                collections.getTotal(), collections.getPageSize(), collections.getCurrentPage()
                );
    }
}
