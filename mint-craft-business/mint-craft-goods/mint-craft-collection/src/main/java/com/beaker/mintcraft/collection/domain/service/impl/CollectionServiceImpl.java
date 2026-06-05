package com.beaker.mintcraft.collection.domain.service.impl;

import cn.hutool.core.lang.Assert;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.api.collection.constant.CollectionInventoryModifyType;
import com.beaker.mintcraft.api.collection.request.admin.*;
import com.beaker.mintcraft.api.collection.request.held.HeldCollectionCreateRequest;
import com.beaker.mintcraft.api.collection.response.CollectionAirdropResponse;
import com.beaker.mintcraft.api.collection.response.CollectionInventoryModifyResponse;
import com.beaker.mintcraft.api.goods.constant.GoodsType;
import com.beaker.mintcraft.api.goods.request.GoodsCancelSaleRequest;
import com.beaker.mintcraft.api.goods.request.GoodsTrySaleRequest;
import com.beaker.mintcraft.collection.domain.entity.*;
import com.beaker.mintcraft.collection.domain.entity.convertor.HeldCollectionConvertor;
import com.beaker.mintcraft.collection.domain.service.CollectionService;
import com.beaker.mintcraft.collection.infrastructure.exception.CollectionException;
import com.beaker.mintcraft.collection.infrastructure.mapper.CollectionAirdropStreamMapper;
import com.beaker.mintcraft.collection.infrastructure.mapper.CollectionInventoryStreamMapper;
import com.beaker.mintcraft.collection.infrastructure.mapper.CollectionMapper;
import com.beaker.mintcraft.collection.infrastructure.mapper.CollectionStreamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.beaker.mintcraft.base.response.ResponseCode.DUPLICATED;
import static com.beaker.mintcraft.base.response.ResponseCode.SUCCESS;
import static com.beaker.mintcraft.collection.infrastructure.exception.CollectionErrorCode.*;

/**
 * @Author beaker
 * @Date 2026/5/10 18:01
 * @Description 藏品服务实现类
 */
public abstract class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements CollectionService {

    @Autowired
    private HeldCollectionService heldCollectionService;

    @Autowired
    private CollectionInventoryStreamMapper collectionInventoryStreamMapper;

    @Autowired
    private CollectionAirdropStreamMapper collectionAirdropStreamMapper;

    @Autowired
    private CollectionStreamMapper collectionStreamMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Collection create(CollectionCreateRequest request) {
        // 创建藏品
        Collection collection = Collection.create(request);

        // 保存藏品
        boolean saveResult = this.save(collection);
        Assert.isTrue(saveResult, () -> new CollectionException(COLLECTION_SAVE_FAILED));

        // TODO: 补充快照相关逻辑

        // 保存藏品操作流水
        CollectionStream stream = new CollectionStream(collection, request.getIdentifier(), request.getEventType());
        saveResult = collectionStreamMapper.insert(stream) == 1;
        Assert.isTrue(saveResult, () -> new CollectionException(COLLECTION_STREAM_SAVE_FAILED));

        return collection;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CollectionInventoryModifyResponse modifyInventory(CollectionModifyInventoryRequest request) {
        CollectionInventoryModifyResponse response = new CollectionInventoryModifyResponse();
        response.setCollectionId(request.getCollectionId());

        // 幂等校验
        CollectionStream existStream = collectionStreamMapper
                .selectByIdentifier(request.getIdentifier(), request.getEventType().name(), request.getCollectionId());
        if (existStream != null) {
            response.setSuccess(true);
            response.setResponseCode(DUPLICATED.name());

            return response;
        }

        // 获取藏品最新状态
        Collection collection = getById(request.getCollectionId());
        if (collection == null) {
            throw new CollectionException(COLLECTION_QUERY_FAIL);
        }

        int quantityDiff = request.getQuantity() - collection.getQuantity();
        response.setQuantityModified(Math.abs(quantityDiff));

        // 根据不同情况修改库存
        if (quantityDiff == 0) {
            response.setModifyType(CollectionInventoryModifyType.UNMODIFIED);
            response.setSuccess(true);

            return response;
        } else if (quantityDiff > 0) {
            response.setModifyType(CollectionInventoryModifyType.INCREASE);
        } else {
            response.setModifyType(CollectionInventoryModifyType.DECREASE);
        }
        long oldSaleableInventory = collection.getSaleableInventory();
        collection.setQuantity(request.getQuantity());
        collection.setSaleableInventory(oldSaleableInventory + quantityDiff);

        // 更新藏品
        boolean updateResult = updateById(collection);
        Assert.isTrue(updateResult, () -> new CollectionException(COLLECTION_UPDATE_FAILED));

        // 插入流水
        CollectionInventoryStream inventoryStream = new CollectionInventoryStream(collection, request.getIdentifier(), request.getEventType(), quantityDiff);
        boolean saveResult = collectionInventoryStreamMapper.insert(inventoryStream) == 1;
        Assert.isTrue(saveResult, () -> new CollectionException(COLLECTION_INVENTORY_UPDATE_FAILED));

        response.setSuccess(true);
        return response;
    }

    @Override
    @CacheInvalidate(name = ":collection:cache:id:", key = "#args[0].collectionId")
    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyPrice(CollectionModifyPriceRequest request) {
        // 幂等校验
        CollectionStream existStream = collectionStreamMapper
                .selectByIdentifier(request.getIdentifier(), request.getEventType().name(), request.getCollectionId());
        if (existStream != null) {
            return true;
        }

        // 更新藏品
        Collection collection = getById(request.getCollectionId());
        collection.setPrice(request.getPrice());
        boolean updateResult = updateById(collection);
        Assert.isTrue(updateResult, () -> new CollectionException(COLLECTION_UPDATE_FAILED));

        // TODO: 后续补充快照

        // 插入流水
        CollectionStream collectionStream = new CollectionStream(collection, request.getIdentifier(), request.getEventType());
        boolean saveResult = collectionStreamMapper.insert(collectionStream) == 1;
        Assert.isTrue(saveResult, () -> new CollectionException(COLLECTION_STREAM_SAVE_FAILED));

        return true;
    }

    @Override
    @CacheInvalidate(name = ":collection:cache:id:", key = "#args[0].collectionId")
    public Boolean remove(CollectionRemoveRequest request) {
        // 幂等校验
        CollectionStream existStream = collectionStreamMapper
                .selectByIdentifier(request.getIdentifier(), request.getEventType().name(), request.getCollectionId());
        if (existStream != null) {
            return true;
        }

        // 更新藏品状态到 REMOVED
        Collection collection = getById(request.getCollectionId());
        collection.remove();
        boolean updateResult = updateById(collection);
        Assert.isTrue(updateResult, () -> new CollectionException(COLLECTION_UPDATE_FAILED));

        // 插入流水
        CollectionStream collectionStream = new CollectionStream(collection, request.getIdentifier(), request.getEventType());
        boolean saveResult = collectionStreamMapper.insert(collectionStream) == 1;
        Assert.isTrue(saveResult, () -> new CollectionException(COLLECTION_STREAM_SAVE_FAILED));

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CollectionAirdropResponse airDrop(CollectionAirDropRequest request, Collection collection) {
        CollectionAirdropResponse response = new CollectionAirdropResponse();

        // 幂等校验
        CollectionAirdropStream existStream = collectionAirdropStreamMapper.selectByIdentifier(request.getIdentifier(), request.getEventType().name(), collection.getId(), request.getRecipientUserId());
        if (existStream != null) {
            response.setSuccess(true);
            response.setResponseCode(DUPLICATED.name());
            response.setAirDropStreamId(existStream.getId());

            return response;
        }

        // 插入流水
        CollectionInventoryStream stream = new CollectionInventoryStream(collection, request.getIdentifier(), request.getEventType(), request.getQuantity());
        int saveResult = collectionInventoryStreamMapper.insert(stream);
        Assert.isTrue(saveResult == 1, () -> new CollectionException(COLLECTION_STREAM_SAVE_FAILED));

        // 插入空投流水
        CollectionAirdropStream airdropStream = new CollectionAirdropStream(collection, request.getIdentifier(), request.getEventType(), request.getQuantity(), request.getRecipientUserId());
        saveResult = collectionAirdropStreamMapper.insert(airdropStream);
        Assert.isTrue(saveResult == 1, () -> new CollectionException(COLLECTION_AIRDROP_STREAM_UPDATE_FAILED));

        // 批量创建藏品
        List<HeldCollectionCreateRequest> heldCollectionCreateRequests = new ArrayList<>();
        for (int i = 1; i <= request.getQuantity(); i++) {
            // 构造请求
            HeldCollectionCreateRequest createRequest = getHeldCollectionCreateRequest(request, collection, airdropStream);

            heldCollectionCreateRequests.add(createRequest);
        }
        List<HeldCollection> heldCollections = heldCollectionService.batchCreate(heldCollectionCreateRequests);

        // 扣减藏品库存
        int updateResult = collectionMapper.airDrop(request.getCollectionId(), request.getQuantity());
        Assert.isTrue(updateResult == 1, () -> new CollectionException(COLLECTION_UPDATE_FAILED));

        response.setSuccess(true);
        response.setResponseCode(SUCCESS.name());
        response.setAirDropStreamId(airdropStream.getId());
        response.setHeldCollections(HeldCollectionConvertor.INSTANCE.mapToVo(heldCollections));

        return response;
    }

    @Override
    @Cached(name = ":collection:cache:id:", expire = 60, localExpire = 10, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH, key = "args[0]", cacheNullValue = true)
    @CacheRefresh(refresh = 50, timeUnit = TimeUnit.MINUTES)
    public Collection queryById(Long collectionId) {
        return getById(collectionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean sale(GoodsTrySaleRequest request) {
        // 幂等校验
        CollectionInventoryStream existStream = collectionInventoryStreamMapper
                .selectByIdentifier(request.identifier(), request.eventType().name(), request.goodsId());
        if (existStream != null) {
            return true;
        }

        // 去数据库查询出最新的值
        Collection collection = this.getById(request.goodsId());

        // 写入 collection 库存流水
        CollectionInventoryStream stream = new CollectionInventoryStream(collection, request.identifier(), request.eventType(), request.quantity());
        int result = collectionInventoryStreamMapper.insert(stream);
        Assert.isTrue(result == 1, () -> new CollectionException(COLLECTION_STREAM_SAVE_FAILED));

        // 核心逻辑执行
        result = collectionMapper.sale(request.goodsId(), request.quantity());
        Assert.isTrue(result == 1, () -> new CollectionException(COLLECTION_SAVE_FAILED));

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancel(GoodsCancelSaleRequest request) {
        // 幂等校验
        CollectionInventoryStream existStream = collectionInventoryStreamMapper
                .selectByIdentifier(request.identifier(), request.eventType().name(), request.collectionId());
        if (existStream != null) {
            return true;
        }

        // 去数据库查询出最新的值
        Collection collection = this.getById(request.collectionId());

        // 写入 collection 库存流水
        CollectionInventoryStream stream = new CollectionInventoryStream(collection, request.identifier(), request.eventType(), request.quantity());
        int result = collectionInventoryStreamMapper.insert(stream);
        Assert.isTrue(result == 1, () -> new CollectionException(COLLECTION_STREAM_SAVE_FAILED));

        // 核心逻辑执行
        result = collectionMapper.cancel(request.collectionId(), request.quantity());
        Assert.isTrue(result == 1, () -> new CollectionException(COLLECTION_SAVE_FAILED));

        return true;
    }

    public HeldCollectionCreateRequest getHeldCollectionCreateRequest(CollectionAirDropRequest airDropRequest, Collection collection, CollectionAirdropStream airdropStream) {
        HeldCollectionCreateRequest request = new HeldCollectionCreateRequest();
        request.setGoodsId(airDropRequest.getCollectionId());
        request.setUserId(airDropRequest.getRecipientUserId());
        request.setName(collection.getName());
        request.setCover(collection.getCover());
        request.setPurchasePrice(collection.getPrice());
        request.setBizType(airDropRequest.getBizType().name());
        request.setBizNo(airdropStream.getId().toString());
        request.setSerialNoBaseId(String.valueOf(collection.getId()));
        request.setGoodsType(GoodsType.COLLECTION.name());

        return request;
    }
}
