package com.beaker.mintcraft.user.domain.service;

/**
 * @Author beaker
 * @Date 2026/5/9 16:11
 * @Description 认证服务
 */
public interface AuthService {

    /**
     * 校验认证信息
     *
     * @param realName
     * @param idCard
     * @return
     */
    public boolean checkAuth(String realName, String idCard);
}
