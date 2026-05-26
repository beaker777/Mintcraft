package com.beaker.mintcraft.api.collection.request.held;

import com.beaker.mintcraft.api.collection.constant.HeldCollectionEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/5/26 17:57
 * @Description 持有藏品激活请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeldCollectionActiveRequest extends BaseHeldCollectionRequest {

    /**
     * 'nftId'
     */
    private String nftId;

    /**
     * 'txHash'
     */
    private String txHash;

    @Override
    public HeldCollectionEventType getEventType() {
        return HeldCollectionEventType.ACTIVE;
    }
}
