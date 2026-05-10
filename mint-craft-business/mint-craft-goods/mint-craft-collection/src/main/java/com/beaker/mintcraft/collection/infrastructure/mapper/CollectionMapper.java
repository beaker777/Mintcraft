package com.beaker.mintcraft.collection.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.collection.domain.entity.Collection;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author beaker
 * @Date 2026/5/10 18:04
 * @Description 藏品 Mapper 接口
 */
@Mapper
public interface CollectionMapper extends BaseMapper<Collection> {
}
