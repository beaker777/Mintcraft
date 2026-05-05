package com.beaker.mintcraft.api.user.constant;

/**
 * @Author beaker
 * @Date 2026/5/5 20:33
 * @Description 用户权限
 */
public enum UserPermission {

    /**
     * 基本权限, 对应 INIT
     */
    BASIC,

    /**
     * 已实名认证权限, 对应 AUTH, ACTIVE
     */
    AUTH,

    /**
     * 已冻结权限, 对应 FROZEN
     */
    FROZEN,

    /**
     * 无任何权限
     */
    NONE;
}
