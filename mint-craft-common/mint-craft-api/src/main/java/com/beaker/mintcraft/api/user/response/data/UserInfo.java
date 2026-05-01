package com.beaker.mintcraft.api.user.response.data;

import com.beaker.mintcraft.api.user.constant.UserRole;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/4/28 20:00
 * @Description 用户信息
 */
@Data
public class UserInfo extends BasicUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    //@SensitiveStrategyPhone
    private String telephone;

    /**
     * 状态
     */
    private String state;

    /**
     * 区块链地址
     */
    private String blockChainUrl;

    /**
     * 区块链平台
     */
    private String blockChainPlatform;

    /**
     * 实名认证
     */
    private Boolean certification;

    /**
     * 用户角色
     */
    private UserRole userRole;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 注册时间
     */
    private Date createTime;

}
