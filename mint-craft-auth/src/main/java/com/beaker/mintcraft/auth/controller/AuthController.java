package com.beaker.mintcraft.auth.controller;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.beaker.mintcraft.api.user.request.UserQueryRequest;
import com.beaker.mintcraft.api.user.request.UserRegisterRequest;
import com.beaker.mintcraft.api.user.response.UserOperatorResponse;
import com.beaker.mintcraft.api.user.response.UserQueryResponse;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import com.beaker.mintcraft.auth.param.LoginParam;
import com.beaker.mintcraft.auth.valobj.LoginVO;
import com.beaker.mintcraft.web.vo.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author beaker
 * @Date 2026/4/28 19:39
 * @Description 认证接口
 */
@Slf4j
@RestController
@RequestMapping("auth")
public class AuthController {

    @DubboReference
    private UserFacadeService userFacadeService;

    // 测试用验证码
    private static final String ROOT_CAPTCHA = "8888";

    // token 默认过期时间 7 天
    private static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;

    /**
     * 登录
     *
     * @param loginParam 登录参数: 电话号, 验证码, 邀请者
     * @return
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginParam loginParam) {
        // TODO 为了方便, 暂时跳过验证码校验
        if (!ROOT_CAPTCHA.equals(loginParam.getCaptcha())) {
            // 校验验证码
        }

        // 根据电话号查询用户信息
        UserQueryRequest userQueryRequest = new UserQueryRequest(loginParam.getTelephone());
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
        UserInfo userInfo = userQueryResponse.getData();
        // 判断是需要注册还是直接登录
        if (userInfo == null) {
            // 注册
            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
            userRegisterRequest.setTelephone(loginParam.getTelephone());
            userRegisterRequest.setPassword(loginParam.getInviteCode());

            UserOperatorResponse response = userFacadeService.register(userRegisterRequest);
            if (response.getSuccess()) {
                // 如果注册成功, 再去数据库查询一遍
                userQueryResponse = userFacadeService.query(userQueryRequest);
                userInfo = userQueryResponse.getData();

                StpUtil.login(userInfo.getUserId(),
                        new SaLoginModel()
                                .setIsLastingCookie(loginParam.getRememberMe())
                                .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
                // 将用户信息保存到 sa-token 的 session 里
                StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);

                LoginVO loginVO = new LoginVO(userInfo);
                return Result.success(loginVO);
            }

            return Result.error(response.getResponseCode(), response.getResponseMessage());
        } else {
            // 登录
            StpUtil.login(
                    userInfo.getUserId(),
                    new SaLoginModel()
                            .setIsLastingCookie(loginParam.getRememberMe())
                            .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
            // 将用户信息保存到 sa-token 的 session 里
            StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);

            LoginVO loginVO = new LoginVO(userInfo);
            return Result.success(loginVO);
        }
    }
}
