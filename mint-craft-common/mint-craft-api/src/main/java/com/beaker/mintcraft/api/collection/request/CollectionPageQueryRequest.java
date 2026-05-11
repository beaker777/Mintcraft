package com.beaker.mintcraft.api.collection.request;

import com.beaker.mintcraft.base.request.PageRequest;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/11 17:24
 * @Description 藏品分页查询
 */
@Data
public class CollectionPageQueryRequest extends PageRequest {

    private String state;

    private String keyword;
}
