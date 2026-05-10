package com.beaker.mintcraft.collection.facade;

import com.beaker.mintcraft.api.collection.service.CollectionFacadeService;
import com.beaker.mintcraft.api.collection.valobj.CollectionVO;
import com.beaker.mintcraft.base.response.SingleResponse;
import com.beaker.mintcraft.collection.domain.entity.Collection;
import com.beaker.mintcraft.collection.domain.entity.convertor.CollectionConvertor;
import com.beaker.mintcraft.collection.domain.service.CollectionService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import static com.beaker.mintcraft.collection.infrastructure.exception.CollectionErrorCode.COLLECTION_NOT_EXIST;

/**
 * @Author beaker
 * @Date 2026/5/10 18:29
 * @Description 藏品 facade 层实现
 */
@DubboService
public class CollectionFacadeServiceImpl implements CollectionFacadeService {

    @Resource
    private CollectionService collectionService;

    @Override
    public SingleResponse<CollectionVO> queryById(Long collectionId) {
        Collection collection = collectionService.queryById(collectionId);
        if (collection == null) {
            return SingleResponse.fail(COLLECTION_NOT_EXIST.getCode(), COLLECTION_NOT_EXIST.getMessage());
        }

        // TODO: 去查询一遍最新的库存

        CollectionVO collectionVO = CollectionConvertor.INSTANCE.mapToVo(collection);
        collectionVO.setState(collection.getState(), collection.getSaleTime(), collection.getSaleableInventory());

        return SingleResponse.of(collectionVO);
    }
}
