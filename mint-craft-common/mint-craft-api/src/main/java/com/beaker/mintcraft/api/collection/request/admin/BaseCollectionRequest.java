package com.beaker.mintcraft.api.collection.request.admin;

import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import com.beaker.mintcraft.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/5/31 21:01
 * @Description 藏品请求模板
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseCollectionRequest extends BaseRequest {

    /**
     * 幂等号
     */
    @NotNull(message = "identifier is not null")
    private String identifier;

    /**
     * '藏品id'
     */
    private Long collectionId;

    /**
     * 获取事件类型
     * @return
     */
    public abstract GoodsEvent getEventType();
}
