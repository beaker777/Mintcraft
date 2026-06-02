package com.beaker.mintcraft.api.collection.request.held;

import com.beaker.mintcraft.api.collection.constant.HeldCollectionEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/6/2 21:40
 * @Description 持有藏品销毁请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeldCollectionDestroyRequest extends BaseHeldCollectionRequest {

    /**
     * 操作人Id
     */
    private String operatorId;

    @Override
    public HeldCollectionEventType getEventType() {
        return HeldCollectionEventType.DESTROY;
    }
}
