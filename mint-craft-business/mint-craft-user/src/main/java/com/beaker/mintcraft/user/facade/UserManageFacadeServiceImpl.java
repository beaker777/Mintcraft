package com.beaker.mintcraft.user.facade;

import com.beaker.mintcraft.api.user.response.UserOperatorResponse;
import com.beaker.mintcraft.api.user.service.UserManageFacadeService;
import com.beaker.mintcraft.user.domain.service.UserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author beaker
 * @Date 2026/6/2 16:14
 * @Description 用户管理 facade 层实现
 */
@DubboService
public class UserManageFacadeServiceImpl implements UserManageFacadeService {

    @Resource
    private UserService userService;

    @Override
    public UserOperatorResponse freeze(Long userId) {
        return userService.freeze(userId);
    }

    @Override
    public UserOperatorResponse unfreeze(Long userId) {
        return null;
    }
}
