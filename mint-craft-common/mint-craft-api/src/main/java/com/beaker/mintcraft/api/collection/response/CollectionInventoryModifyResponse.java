package com.beaker.mintcraft.api.collection.response;

import com.beaker.mintcraft.api.collection.constant.CollectionInventoryModifyType;
import com.beaker.mintcraft.base.response.BaseResponse;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/6/1 18:37
 * @Description 藏品库存修改响应
 */
@Data
public class CollectionInventoryModifyResponse extends CollectionModifyResponse {

    /**
     * 修改类型
     */
    private CollectionInventoryModifyType modifyType;

    /**
     * 修改的数量
     */
    private Integer quantityModified;
}
