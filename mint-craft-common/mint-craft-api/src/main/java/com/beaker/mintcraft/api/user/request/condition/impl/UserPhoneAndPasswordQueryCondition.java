package com.beaker.mintcraft.api.user.request.condition.impl;

import com.beaker.mintcraft.api.user.request.condition.UserQueryCondition;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/4/28 20:32
 * @Description 根据用户手机号密码查询
 */
@Data
public class UserPhoneAndPasswordQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 用户手机号
     */
    private String telephone;

    /**
     * 密码
     */
    private String password;
}
