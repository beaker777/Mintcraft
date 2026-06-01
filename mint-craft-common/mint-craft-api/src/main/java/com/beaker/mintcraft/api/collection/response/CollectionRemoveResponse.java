package com.beaker.mintcraft.api.collection.response;

import com.beaker.mintcraft.base.response.BaseResponse;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/6/1 20:33
 * @Description 藏品下架响应
 */
@Data
public class CollectionRemoveResponse extends BaseResponse {

    /**
     * 藏品id
     */
    private Long collectionId;
}
