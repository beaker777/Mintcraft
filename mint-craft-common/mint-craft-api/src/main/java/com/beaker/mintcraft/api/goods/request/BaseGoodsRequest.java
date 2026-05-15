package com.beaker.mintcraft.api.goods.request;

import com.beaker.mintcraft.api.goods.constant.GoodsEvent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/15 20:08
 * @Description 基本商品请求
 */
@Data
public abstract class BaseGoodsRequest {

    /**
     * 幂等号
     */
    @NotNull(message = "identifier is not null")
    private String identifier;

    /**
     * '藏品id'
     */
    private Long goodsId;

    /**
     * 藏品类型
     *
     * @link GoodsType
     */
    private String goodsType;

    /**
     * 获取事件类型
     *
     * @return
     */
    public abstract GoodsEvent getEventType();
}
