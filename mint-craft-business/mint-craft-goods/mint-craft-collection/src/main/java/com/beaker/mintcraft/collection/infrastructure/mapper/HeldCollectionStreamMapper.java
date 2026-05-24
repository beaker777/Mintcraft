package com.beaker.mintcraft.collection.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.collection.domain.entity.HeldCollectionStream;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author beaker
 * @Date 2026/5/24 20:47
 * @Description 持有藏品流水 mapper
 */
@Mapper
public interface HeldCollectionStreamMapper extends BaseMapper<HeldCollectionStream> {
}
