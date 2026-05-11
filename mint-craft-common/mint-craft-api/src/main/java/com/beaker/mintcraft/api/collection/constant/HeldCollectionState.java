package com.beaker.mintcraft.api.collection.constant;

/**
 * @Author beaker
 * @Date 2026/5/11 18:34
 * @Description 用户持有藏品状态
 */
public enum HeldCollectionState {

    /**
     * 初始化
     */
    INIT,

    /**
     * 生效
     */
    ACTIVED,

    /**
     * 失效
     */
    INACTIVED,

    /**
     * 销毁中
     */
    DESTROYING,

    /**
     * 已销毁
     */
    DESTROYED;
}
