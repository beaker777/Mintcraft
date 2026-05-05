package com.beaker.mintcraft.api.user.service;

import com.beaker.mintcraft.api.user.request.UserQueryRequest;
import com.beaker.mintcraft.api.user.request.UserRegisterRequest;
import com.beaker.mintcraft.api.user.response.UserOperatorResponse;
import com.beaker.mintcraft.api.user.response.UserQueryResponse;
import com.beaker.mintcraft.api.user.response.data.UserInfo;

/**
 * @Author beaker
 * @Date 2026/4/29 14:05
 * @Description 用户模块 facade 层
 */
public interface UserFacadeService {

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    UserOperatorResponse register(UserRegisterRequest userRegisterRequest);

    /**
     * 用户信息查询
     *
     * @param userQueryRequest
     * @return
     */
    UserQueryResponse<UserInfo> query(UserQueryRequest userQueryRequest);
}
