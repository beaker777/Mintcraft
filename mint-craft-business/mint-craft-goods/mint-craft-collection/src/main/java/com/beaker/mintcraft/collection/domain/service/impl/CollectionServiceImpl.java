package com.beaker.mintcraft.collection.domain.service.impl;

import cn.hutool.core.lang.Assert;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.api.goods.request.GoodsCancelSaleRequest;
import com.beaker.mintcraft.api.goods.request.GoodsTrySaleRequest;
import com.beaker.mintcraft.collection.domain.entity.Collection;
import com.beaker.mintcraft.collection.domain.entity.CollectionInventoryStream;
import com.beaker.mintcraft.collection.domain.service.CollectionService;
import com.beaker.mintcraft.collection.infrastructure.exception.CollectionException;
import com.beaker.mintcraft.collection.infrastructure.mapper.CollectionInventoryStreamMapper;
import com.beaker.mintcraft.collection.infrastructure.mapper.CollectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.beaker.mintcraft.collection.infrastructure.exception.CollectionErrorCode.COLLECTION_SAVE_FAILED;
import static com.beaker.mintcraft.collection.infrastructure.exception.CollectionErrorCode.COLLECTION_STREAM_SAVE_FAILED;

/**
 * @Author beaker
 * @Date 2026/5/10 18:01
 * @Description 藏品服务实现类
 */
public abstract class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements CollectionService {

    @Autowired
    private CollectionInventoryStreamMapper collectionInventoryStreamMapper;

    @Autowired
    private CollectionMapper collectionMapper;

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
}
