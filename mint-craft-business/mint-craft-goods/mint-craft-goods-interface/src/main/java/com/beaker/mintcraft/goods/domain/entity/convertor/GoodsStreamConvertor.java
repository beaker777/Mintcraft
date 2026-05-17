package com.beaker.mintcraft.goods.domain.entity.convertor;

import com.beaker.mintcraft.api.goods.valobj.GoodsStreamVO;
import com.beaker.mintcraft.collection.domain.entity.CollectionInventoryStream;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @Author beaker
 * @Date 2026/5/17 21:48
 * @Description 商品库存流水转换类
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface GoodsStreamConvertor {

    public static GoodsStreamConvertor  INSTANCE = Mappers.getMapper(GoodsStreamConvertor.class);

    /**
     * 转换实体
     *
     * @param request
     * @return
     */
    @Mapping(target = "goodsId", source = "request.collectionId")
    @Mapping(target = "goodsType", constant = "COLLECTION")
    public GoodsStreamVO mapToVo(CollectionInventoryStream request);

}
