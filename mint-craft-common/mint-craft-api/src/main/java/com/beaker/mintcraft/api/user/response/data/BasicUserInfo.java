package com.beaker.mintcraft.api.user.response.data;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author beaker
 * @Date 2026/4/28 20:03
 * @Description 基本的用户信息, 避免返回过多字段
 */
@Data
public class BasicUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像地址
     */
    private String profilePhotoUrl;
}
