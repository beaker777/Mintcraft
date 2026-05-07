package com.beaker.mintcraft.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.beaker.mintcraft.api.user.param.UserModifyParam;
import com.beaker.mintcraft.api.user.request.UserModifyRequest;
import com.beaker.mintcraft.api.user.response.data.BasicUserInfo;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.user.domain.entity.User;
import com.beaker.mintcraft.user.domain.entity.convertor.UserConvertor;
import com.beaker.mintcraft.user.domain.service.UserService;
import com.beaker.mintcraft.user.infrastructure.exception.UserException;
import com.beaker.mintcraft.web.vo.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.beaker.mintcraft.user.infrastructure.exception.UserErrorCode.USER_NOT_EXIST;
import static com.beaker.mintcraft.user.infrastructure.exception.UserErrorCode.USER_PASSWD_CHECK_FAIL;

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

    @GetMapping("/queryUserByTel")
    public Result<BasicUserInfo> queryUserByTel(String telephone) {
        // 通过电话号获取 user
        User user = userService.findByTelephone(telephone);

        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }

        return Result.success(UserConvertor.INSTANCE.mapToBasicVo(user));
    }

    @PostMapping("/modifyNickName")
    public Result<Boolean> modifyNiceName(@Valid @RequestBody UserModifyParam userModifyParam) {
        String userId = (String) StpUtil.getLoginId();

        // 修改信息
        UserModifyRequest userModifyRequest = new UserModifyRequest();
        userModifyRequest.setUserId(Long.valueOf(userId));
        userModifyRequest.setNickName(userModifyParam.getNickName());

        Boolean modifyResult = userService.modify(userModifyRequest).getSuccess();
        return Result.success(modifyResult);
    }

    @PostMapping("/modifyPassword")
    public Result<Boolean> modifyPassword(@Valid @RequestBody UserModifyParam userModifyParam) {
        // 获取用户信息
        String userId = (String) StpUtil.getLoginId();
        User user = userService.findById(Long.valueOf(userId));

        // 校验旧密码是否正确
        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
        if (!StringUtils.equals(user.getPasswordHash(), DigestUtil.md5Hex(userModifyParam.getOldPassword()))) {
            throw new UserException(USER_PASSWD_CHECK_FAIL);
        }

        // 修改信息
        UserModifyRequest userModifyRequest = new UserModifyRequest();
        userModifyRequest.setUserId(Long.valueOf(userId));
        userModifyRequest.setPassword(userModifyParam.getNewPassword());

        Boolean modifyResult = userService.modify(userModifyRequest).getSuccess();
        return Result.success(modifyResult);
    }
}
