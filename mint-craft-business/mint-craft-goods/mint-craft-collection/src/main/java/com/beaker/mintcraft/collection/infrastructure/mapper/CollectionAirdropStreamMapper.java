package com.beaker.mintcraft.collection.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.collection.domain.entity.CollectionAirdropStream;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author beaker
 * @Date 2026/6/5 20:31
 * @Description 藏品空投流水 mapper
 */
@Mapper
public interface CollectionAirdropStreamMapper extends BaseMapper<CollectionAirdropStream> {

    /**
     * 根据标识符查询
     *
     * @param identifier
     * @param streamType
     * @param collectionId
     * @param recipientUserId
     * @return
     */
    CollectionAirdropStream selectByIdentifier(String identifier, String streamType, Long collectionId, String recipientUserId);

}
