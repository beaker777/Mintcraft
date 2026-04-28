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
}
