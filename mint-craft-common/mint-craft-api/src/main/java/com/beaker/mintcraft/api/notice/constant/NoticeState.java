package com.beaker.mintcraft.api.notice.constant;

/**
 * @Author beaker
 * @Date 2026/5/8 14:31
 * @Description 通知状态
 */
public enum NoticeState {

    /**
     * 初始化
     */
    INIT,

    /**
     * 已发送成功
     */
    SUCCESS,

    /**
     * 发送失败
     */
    FAILED,

    /**
     * 已挂起
     */
    SUSPENDED;
}
