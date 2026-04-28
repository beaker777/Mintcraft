package com.beaker.mintcraft.user.controller;

import com.beaker.mintcraft.user.domain.entity.User;
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
    public Result<User> getUserInfo(@RequestParam(value = "userId") Long userId) {
        User user = userService.findById(userId);

        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }

        return Result.success(user);
    }
}
