package com.beaker.mintcraft.collection.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.collection.domain.entity.CollectionStream;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author beaker
 * @Date 2026/5/31 21:19
 * @Description 藏品流水 mapper
 */
@Mapper
public interface CollectionStreamMapper extends BaseMapper<CollectionStream> {
}
