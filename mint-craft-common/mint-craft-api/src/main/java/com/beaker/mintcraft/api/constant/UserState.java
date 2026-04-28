package com.beaker.mintcraft.api.constant;

/**
 * @Author beaker
 * @Date 2026/4/28 17:39
 * @Description 用户状态
 */
public enum UserState {

    /**
     * 创建成功
     */
    INIT,

    /**
     * 实名认证
     */
    AUTH,

    /**
     * 上链成功
     */
    ACTIVE,

    /**
     * 冻结
     */
    FROZEN;
}
