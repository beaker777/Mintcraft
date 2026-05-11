package com.beaker.mintcraft.collection.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.collection.domain.entity.HeldCollection;
import com.beaker.mintcraft.collection.infrastructure.mapper.HeldCollectionMapper;
import org.springframework.stereotype.Service;

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
}
