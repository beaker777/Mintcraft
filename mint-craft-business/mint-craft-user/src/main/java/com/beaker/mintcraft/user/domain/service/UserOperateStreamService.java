package com.beaker.mintcraft.user.domain.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.api.user.constant.UserOperateTypeEnum;
import com.beaker.mintcraft.user.domain.entity.User;
import com.beaker.mintcraft.user.domain.entity.UserOperateStream;
import com.beaker.mintcraft.user.infrastructure.mapper.UserOperateStreamMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/5/7 15:27
 * @Description 用户操作流水服务
 */
@Service
public class UserOperateStreamService extends ServiceImpl<UserOperateStreamMapper, UserOperateStream> {

    public Long insertStream(User user, UserOperateTypeEnum type) {
        UserOperateStream stream = new UserOperateStream();
        stream.setUserId(String.valueOf(user.getId()));
        stream.setOperateTime(new Date());
        stream.setType(type.name());
        stream.setParam(JSON.toJSONString(user));

        boolean result = save(stream);
        if (result) {
            return stream.getId();
        }
        return null;
    }
}
