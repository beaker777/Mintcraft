package com.beaker.mintcraft.collection.domain.entity.convertor;

import com.beaker.mintcraft.api.collection.valobj.HeldCollectionVO;
import com.beaker.mintcraft.collection.domain.entity.HeldCollection;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/5/11 21:25
 * @Description 用户持有藏品转换器
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface HeldCollectionConvertor {

    HeldCollectionConvertor INSTANCE = Mappers.getMapper(HeldCollectionConvertor.class);

    /**
     * 转换为vo
     *
     * @param request
     * @return
     */
    public HeldCollectionVO mapToVo(HeldCollection request);

    /**
     * 转换为vo
     *
     * @param request
     * @return
     */
    public List<HeldCollectionVO> mapToVo(List<HeldCollection> request);

    /**
     * 转换为实体
     *
     * @param request
     * @return
     */
    public HeldCollection mapToEntity(HeldCollectionVO request);
}
