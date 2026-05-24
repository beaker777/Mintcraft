package com.beaker.mintcraft.api.collection.request.held;

import com.beaker.mintcraft.api.collection.constant.HeldCollectionEventType;
import com.beaker.mintcraft.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/5/24 20:21
 * @Description 持有藏品基础请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseHeldCollectionRequest extends BaseRequest {

    /**
     * 幂等号
     */
    private String identifier;

    /**
     * '持有藏品id'
     */
    private String heldCollectionId;

    /**
     * 事件类型
     *
     * @return
     */
    public abstract HeldCollectionEventType getEventType();
}
