package com.beaker.mintcraft.collection.domain.entity;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.beaker.mintcraft.api.collection.constant.CollectionRarity;
import com.beaker.mintcraft.api.collection.constant.GoodsSaleBizType;
import com.beaker.mintcraft.api.collection.constant.HeldCollectionState;
import com.beaker.mintcraft.api.collection.request.held.HeldCollectionCreateRequest;
import com.beaker.mintcraft.api.common.constant.BusinessCode;
import com.beaker.mintcraft.datasource.domain.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/5/11 18:29
 * @Description 用户持有藏品
 */
@Data
@TableName("held_collection")
public class HeldCollection extends BaseEntity {


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
     * '藏品id'
     */
    private Long collectionId;

    /**
     * '藏品编号'
     */
    private String serialNo;

    /**
     * 'nft唯一编号'
     */
    private String nftId;

    /**
     * '上一个持有人id'
     */
    private String preId;

    /**
     * '持有人id'
     */
    private String userId;

    /**
     * '状态'
     */
    private HeldCollectionState state;

    /**
     * '交易hash'
     */
    private String txHash;

    /**
     * '藏品持有时间'
     */
    private Date holdTime;

    /**
     * '藏品同步时间'
     */
    private Date syncChainTime;

    /**
     * '藏品销毁时间'
     */
    private Date deleteTime;

    /**
     * '业务类型'
     */
    private GoodsSaleBizType bizType;

    /**
     * '业务编号'
     */
    private String bizNo;

    public HeldCollection init(HeldCollectionCreateRequest request, String serialNo) {
        // ShardingJDBC 不支持批量插入时获取并返回主键, 这里单独使用雪花算法生成id
        super.setId(IdUtil.getSnowflake(BusinessCode.HELD_COLLECTION.code()).nextId());

        this.collectionId = request.getGoodsId();
        this.serialNo = serialNo;
        this.userId = request.getUserId();
        this.state = HeldCollectionState.INIT;
        this.holdTime = new Date();
        this.bizNo = request.getBizNo();
        this.bizType = GoodsSaleBizType.valueOf(request.getBizType());
        this.name = request.getName();
        this.cover = request.getCover();
        this.purchasePrice = request.getPurchasePrice();
        this.referencePrice = request.getReferencePrice();
        this.rarity = request.getRarity();

        return this;
    }
}
