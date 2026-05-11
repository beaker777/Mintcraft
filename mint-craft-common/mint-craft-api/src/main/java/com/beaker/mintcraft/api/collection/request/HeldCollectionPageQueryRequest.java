package com.beaker.mintcraft.api.collection.request;

import com.beaker.mintcraft.base.request.PageRequest;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/11 22:07
 * @Description 持有藏品分页查询
 */
@Data
public class HeldCollectionPageQueryRequest extends PageRequest {

    private String state;

    private String userId;

    private String keyword;
}
