package com.beaker.mintcraft.api.user.request.condition.impl;

import com.beaker.mintcraft.api.user.request.condition.UserQueryCondition;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/4/28 20:32
 * @Description 根据用户电话号查询
 */
@Data
public class UserPhoneQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 用户手机号
     */
    private String telephone;
}
