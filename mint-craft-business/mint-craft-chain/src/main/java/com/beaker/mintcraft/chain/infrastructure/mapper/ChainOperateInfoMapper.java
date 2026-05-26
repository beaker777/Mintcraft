package com.beaker.mintcraft.chain.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.chain.domain.entity.ChainOperateInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/5/25 20:55
 * @Description 链操作信息 mapper
 */
@Mapper
public interface ChainOperateInfoMapper extends BaseMapper<ChainOperateInfo> {

    /**
     * 扫描所有
     *
     * @return
     */
    List<ChainOperateInfo> scanAll();

    /**
     * 根据 ID 查询出最小的 ID
     * @param state
     * @return
     */
    public Long queryMinIdByState(String state);
}
