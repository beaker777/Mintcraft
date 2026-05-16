package com.beaker.mintcraft.collection.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.collection.domain.entity.CollectionInventoryStream;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author beaker
 * @Date 2026/5/16 15:19
 * @Description 藏品流水 mapper
 */
@Mapper
public interface CollectionInventoryStreamMapper extends BaseMapper<CollectionInventoryStream> {

    /**
     * 根据标识符查询
     *
     * @param identifier
     * @param streamType
     * @param collectionId
     * @return
     */
    CollectionInventoryStream selectByIdentifier(String identifier, String streamType, Long collectionId);
}
