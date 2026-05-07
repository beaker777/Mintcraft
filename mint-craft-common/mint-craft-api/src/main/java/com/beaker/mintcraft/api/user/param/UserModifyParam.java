package com.beaker.mintcraft.api.user.param;

import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/7 16:49
 * @Description 用户修改参数
 */
@Data
public class UserModifyParam {

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
}