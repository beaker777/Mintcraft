package com.beaker.mintcraft.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beaker.mintcraft.user.domain.entity.UserOperateStream;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author beaker
 * @Date 2026/5/7 15:30
 * @Description 用户操作流水接口
 */
@Mapper
public interface UserOperateStreamMapper extends BaseMapper<UserOperateStream> {
}
