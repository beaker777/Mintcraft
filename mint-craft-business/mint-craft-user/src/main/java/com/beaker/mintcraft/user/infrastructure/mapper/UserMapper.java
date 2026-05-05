package com.beaker.mintcraft.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.user.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author beaker
 * @Date 2026/4/28 17:41
 * @Description 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户 id 查询用户
     *
     * @param id
     * @return
     */
    User findById(Long id);

    /**
     * 根据用户电话号查询用户
     *
     * @param telephone
     * @return
     */
    User findByTelephone(String telephone);

    /**
     * 根据电话号和密码查询用户
     *
     * @param telephone
     * @param passwordHash
     * @return
     */
    User findByTelephoneAndPasswordHash(String telephone, String passwordHash);

    /**
     * 根据用户名查询用户
     *
     * @param nickName
     * @return
     */
    User findByNickName(String nickName);

    /**
     * 根据邀请码查询用户
     *
     * @param inviteCode
     * @return
     */
    User findByInviteCode(String inviteCode);
}
