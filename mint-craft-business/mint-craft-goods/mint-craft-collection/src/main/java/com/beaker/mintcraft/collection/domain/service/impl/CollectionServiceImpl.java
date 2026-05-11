package com.beaker.mintcraft.collection.domain.service.impl;

import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.collection.domain.entity.Collection;
import com.beaker.mintcraft.collection.domain.service.CollectionService;
import com.beaker.mintcraft.collection.infrastructure.mapper.CollectionMapper;

import java.util.concurrent.TimeUnit;

/**
 * @Author beaker
 * @Date 2026/5/10 18:01
 * @Description 藏品服务实现类
 */
public abstract class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements CollectionService {

    @Override
    @Cached(name = ":collection:cache:id:", expire = 60, localExpire = 10, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH, key = "args[0]", cacheNullValue = true)
    @CacheRefresh(refresh = 50, timeUnit = TimeUnit.MINUTES)
    public Collection queryById(Long collectionId) {
        return getById(collectionId);
    }
}
