package com.beaker.mintcraft.api.user.request.condition.impl;

import com.beaker.mintcraft.api.user.request.condition.UserQueryCondition;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/4/28 20:29
 * @Description 根据用户 id 查询
 */
@Data
public class UserIdQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;
}
