package com.beaker.mintcraft.api.chain.constant;

/**
 * @Author beaker
 * @Date 2026/5/25 21:10
 * @Description 链操作类型
 */
public enum ChainOperateType {

    /**
     * 用户创建
     */
    USER_CREATE,

    /**
     * 藏品上链
     */
    COLLECTION_CHAIN,

    /**
     * 盲盒藏品上链
     */
    BLIND_BOX_CHAIN,

    /**
     * 藏品铸造
     */
    COLLECTION_MINT,

    /**
     * 藏品交易
     */
    COLLECTION_TRANSFER,

    /**
     * 藏品销毁
     */
    COLLECTION_DESTROY,

    /**
     * 藏品查询
     */
    COLLECTION_QUERY,
}
