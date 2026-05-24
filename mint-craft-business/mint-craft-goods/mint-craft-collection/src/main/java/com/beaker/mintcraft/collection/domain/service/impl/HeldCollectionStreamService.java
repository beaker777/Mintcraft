package com.beaker.mintcraft.collection.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.collection.domain.entity.HeldCollectionStream;
import com.beaker.mintcraft.collection.infrastructure.mapper.HeldCollectionStreamMapper;
import org.springframework.stereotype.Service;

/**
 * @Author beaker
 * @Date 2026/5/24 20:46
 * @Description 持有藏品流水服务
 */
@Service
public class HeldCollectionStreamService extends ServiceImpl<HeldCollectionStreamMapper, HeldCollectionStream> {

    /**
     * 按照幂等号、持有藏品ID和流水类型查询
     */
    public HeldCollectionStream queryByIdAndStreamType(Long heldCollectionId, String streamType, String identifier) {
        QueryWrapper<HeldCollectionStream> wrapper = new QueryWrapper<>();

        // 包装查询条件
        wrapper.eq("held_collection_id", heldCollectionId);
        wrapper.eq("stream_type", streamType);
        wrapper.eq("identifier", identifier);

        return baseMapper.selectOne(wrapper);
    }
}
