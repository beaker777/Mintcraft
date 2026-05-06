package com.beaker.mintcraft.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.user.domain.entity.User;
import com.beaker.mintcraft.user.domain.entity.convertor.UserConvertor;
import com.beaker.mintcraft.user.domain.service.UserService;
import com.beaker.mintcraft.user.infrastructure.exception.UserException;
import com.beaker.mintcraft.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.beaker.mintcraft.user.infrastructure.exception.UserErrorCode.USER_NOT_EXIST;

/**
 * @Author beaker
 * @Date 2026/4/27 21:35
 * @Description 用户接口
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserInfo")
    public Result<UserInfo> getUserInfo() {
        // 通过 sa-token 获取 userId
        String userId = (String) StpUtil.getLoginId();
        User user = userService.findById(Long.valueOf(userId));

        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }

        return Result.success(UserConvertor.INSTANCE.mapToVO(user));
    }
}
