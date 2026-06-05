package com.beaker.mintcraft.api.collection.response;

import com.beaker.mintcraft.api.collection.valobj.HeldCollectionVO;
import com.beaker.mintcraft.base.response.BaseResponse;
import lombok.Data;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/6/5 20:13
 * @Description 藏品空投相应类
 */
@Data
public class CollectionAirdropResponse extends BaseResponse {

    /**
     * 持有藏品信息
     */
    private List<HeldCollectionVO> heldCollections;

    /**
     * 空投流水id
     */
    private Long airDropStreamId;
}
