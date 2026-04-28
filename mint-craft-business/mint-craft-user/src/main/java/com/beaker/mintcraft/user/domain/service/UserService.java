package com.beaker.mintcraft.user.domain.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.user.domain.entity.User;
import com.beaker.mintcraft.user.infrastructure.mapper.UserMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author beaker
 * @Date 2026/4/27 21:37
 * @Description 用户服务
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements InitializingBean {

    @Autowired
    private UserMapper userMapper;

    /**
     * 通过用户 id 查询详细信息
     *
     * @param userId
     * @return
     */
    public User findById(Long userId) {
        return userMapper.findById(userId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
