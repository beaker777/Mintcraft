package com.beaker.mintcraft.gateway.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.beaker.mintcraft.api.user.constant.UserPermission;
import com.beaker.mintcraft.api.user.constant.UserRole;
import com.beaker.mintcraft.api.user.constant.UserState;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/5/5 20:17
 * @Description 自定义权限验证接口
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 根据 loginId 在用户对应的 session 获取到 userInfo
        UserInfo userInfo = (UserInfo) StpUtil.getSessionByLoginId(loginId).get((String) loginId);

        // admin 用户或状态为 auth active 的用户
        if (userInfo.getUserRole() == UserRole.ADMIN ||
                userInfo.getState().equals(UserState.ACTIVE.name()) || userInfo.getState().equals(UserState.AUTH.name())) {
            return List.of(UserPermission.BASIC.name(), UserPermission.AUTH.name());
        }

        // 状态为 init 的用户
        if (userInfo.getState().equals(UserState.INIT.name())) {
            return List.of(UserPermission.BASIC.name());
        }

        // 状态为 frozen 的用户
        if (userInfo.getState().equals(UserState.FROZEN.name())) {
            return List.of(UserPermission.FROZEN.name());
        }

        return List.of(UserPermission.NONE.name());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 根据 loginId 在用户对应的 session 获取到 userInfo
        UserInfo userInfo = (UserInfo) StpUtil.getSessionByLoginId(loginId).get((String) loginId);

        // admin 用户
        if (userInfo.getUserRole() == UserRole.ADMIN) {
            return List.of(UserRole.ADMIN.name());
        }

        return List.of(UserRole.CUSTOMER.name());
    }
}
