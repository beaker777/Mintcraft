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

    /**
     * 库存扣减
     *
     * @param id
     * @param quantity
     * @return
     */
    int sale(Long id, Integer quantity);

    /**
     * 库存退回
     *
     * @param id
     * @param quantity
     * @return
     */
    int cancel(Long id, Integer quantity);

    /**
     * 空投
     *
     * @param id
     * @param quantity
     * @return
     */
    int airDrop(Long id, Integer quantity);
}
