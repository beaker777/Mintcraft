package com.beaker.mintcraft.api.collection.response;

import com.beaker.mintcraft.base.response.BaseResponse;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/31 21:08
 * @Description 藏品链操作响应
 */
@Data
public class CollectionChainResponse extends BaseResponse {

    /**
     * 藏品id
     */
    private Long collectionId;
}
