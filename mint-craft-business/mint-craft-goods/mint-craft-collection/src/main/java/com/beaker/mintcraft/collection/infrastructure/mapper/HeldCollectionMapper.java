package com.beaker.mintcraft.collection.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.collection.domain.entity.HeldCollection;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author beaker
 * @Date 2026/5/11 20:59
 * @Description 用户持有藏品 Mapper 接口
 */
@Mapper
public interface HeldCollectionMapper extends BaseMapper<HeldCollection> {

    /**
     * 查询出需要重新上链铸造的最小id
     *
     * @return
     */
    public Long queryMinIdForMint();

}
