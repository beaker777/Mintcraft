package com.beaker.mintcraft.api.user.service;

import com.beaker.mintcraft.api.user.response.UserOperatorResponse;

/**
 * @Author beaker
 * @Date 2026/6/2 16:10
 * @Description 用户管理 facade 层接口
 */
public interface UserManageFacadeService {

    /**
     * 用户冻结
     *
     * @param userId
     * @return
     */
    UserOperatorResponse freeze(Long userId);

    /**
     * 用户解冻
     *
     * @param userId
     * @return
     */
    UserOperatorResponse unfreeze(Long userId);

}
