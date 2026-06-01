package com.beaker.mintcraft.api.collection.response;

import com.beaker.mintcraft.base.response.BaseResponse;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/6/1 18:35
 * @Description 藏品修改响应
 */
@Data
public class CollectionModifyResponse extends BaseResponse {

    /**
     * 藏品id
     */
    private Long collectionId;
}
