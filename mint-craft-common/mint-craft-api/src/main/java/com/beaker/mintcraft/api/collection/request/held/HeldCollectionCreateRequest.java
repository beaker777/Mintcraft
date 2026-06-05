package com.beaker.mintcraft.api.collection.request.held;

import com.beaker.mintcraft.api.collection.constant.CollectionRarity;
import com.beaker.mintcraft.api.collection.constant.HeldCollectionEventType;
import com.beaker.mintcraft.api.collection.request.admin.CollectionAirDropRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * @Author beaker
 * @Date 2026/5/24 20:24
 * @Description 持有藏品创建请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeldCollectionCreateRequest extends BaseHeldCollectionRequest {

    /**
     * 藏品名称
     */
    private String name;

    /**
     * 藏品封面
     */
    private String cover;

    /**
     * 购入价格
     */
    private BigDecimal purchasePrice;

    /**
     * 参考价格
     */
    private BigDecimal referencePrice;

    /**
     * 稀有度
     */
    private CollectionRarity rarity;

    /**
     * 商品 id
     */
    private Long goodsId;

    /**
     * 商品类型
     */
    private String goodsType;

    /**
     * '持有人id'
     */
    private String userId;

    /**
     * '藏品编号'
     *
     * @deprecated 外部不要在传入这个值了，不再使用，改为内部自己计算
     */
    @Deprecated
    private String serialNo;

    /**
     * 序列号生成的 baseId，在商品为藏品时，该 id 为藏品 id，在商品为盲盒时，该 id 为盲盒 id
     */
    private String serialNoBaseId;

    /**
     * '业务Id'
     */
    private String bizNo;

    /**
     * '业务类型'
     */
    private String bizType;

    @Override
    public HeldCollectionEventType getEventType() {
        return HeldCollectionEventType.CREATE;
    }
}
