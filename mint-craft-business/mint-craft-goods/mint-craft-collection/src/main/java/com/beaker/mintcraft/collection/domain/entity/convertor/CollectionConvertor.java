package com.beaker.mintcraft.collection.domain.entity.convertor;

import com.beaker.mintcraft.api.collection.constant.CollectionState;
import com.beaker.mintcraft.api.collection.valobj.CollectionVO;
import com.beaker.mintcraft.api.goods.constant.GoodsState;
import com.beaker.mintcraft.collection.domain.entity.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.List;

/**
 * @Author beaker
 * @Date 2026/5/10 19:45
 * @Description 藏品类转换器
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CollectionConvertor {

    CollectionConvertor INSTANCE = Mappers.getMapper(CollectionConvertor.class);

    /**
     * 转换为VO
     *
     * @param request
     * @return
     */
    @Mapping(target = "inventory", source = "request.saleableInventory")
    @Mapping(target = "state", expression = "java(setState(request.getState(), request.getSaleTime(), request.getSaleableInventory()))")
    public CollectionVO mapToVo(Collection request);


    /**
     * 转换为VO
     *
     * @param request
     * @return
     */
    public List<CollectionVO> mapToVo(List<Collection> request);

    /**
     * 转换为实体
     * @param request
     * @return
     */
    @Mapping(target = "saleableInventory", source = "request.inventory")
    @Mapping(target = "state", ignore = true)
    public Collection mapToEntity(CollectionVO request);

    /**
     * 设置状态
     * @param state
     * @param saleTime
     * @param saleableInventory
     * @return
     */
    public default GoodsState setState(CollectionState state, Date saleTime, Long saleableInventory) {
        return CollectionVO.getState(state, saleTime, saleableInventory);
    }
}
