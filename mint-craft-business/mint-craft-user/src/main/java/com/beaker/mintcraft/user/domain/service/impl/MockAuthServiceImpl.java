package com.beaker.mintcraft.user.domain.service.impl;

import com.beaker.mintcraft.user.domain.service.AuthService;

/**
 * @Author beaker
 * @Date 2026/5/9 16:29
 * @Description 实名认证 mock 实现类
 */
public class MockAuthServiceImpl implements AuthService {

    @Override
    public boolean checkAuth(String realName, String idCard) {
        return true;
    }
}
