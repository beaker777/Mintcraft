package com.beaker.mintcraft.collection.domain.service.impl;

import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.api.collection.request.HeldCollectionPageQueryRequest;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.collection.domain.entity.HeldCollection;
import com.beaker.mintcraft.collection.infrastructure.mapper.HeldCollectionMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author beaker
 * @Date 2026/5/11 20:59
 * @Description 用户持有藏品服务
 */
@Service
public class HeldCollectionService extends ServiceImpl<HeldCollectionMapper, HeldCollection> {

    public long queryHeldCollectionCount(String userId) {
        QueryWrapper<HeldCollection> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        return this.count(wrapper);
    }

    @Cached(name = ":held_collection:cache:id:", expire = 60, localExpire = 10, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH, key = "args[0]", cacheNullValue = true)
    @CacheRefresh(refresh = 50, timeUnit = TimeUnit.MINUTES)
    public HeldCollection queryById(Long heldCollectionId) {
        return getById(heldCollectionId);
    }

    public PageResponse<HeldCollection> pageQueryByState(HeldCollectionPageQueryRequest request) {
        Page<HeldCollection> page = new Page<>();
        QueryWrapper<HeldCollection> wrapper = new QueryWrapper<>();

        // 包装查询条件
        wrapper.eq("user_id", request.getUserId());
        if (StringUtils.isNotBlank(request.getState())) {
            wrapper.eq("state", request.getState());
        }
        if (StringUtils.isNotBlank(request.getKeyword())) {
            wrapper.like("name", request.getKeyword());
        }
        wrapper.orderBy(true, false, "gmt_create");

        // 分页查询
        Page<HeldCollection> collections = this.page(page, wrapper);

        return PageResponse.of(collections.getRecords(), (int) collections.getTotal(), request.getPageSize(), request.getCurrentPage());
    }
}
