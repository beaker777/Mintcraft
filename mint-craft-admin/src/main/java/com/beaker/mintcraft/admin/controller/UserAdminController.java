package com.beaker.mintcraft.admin.controller;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.beaker.mintcraft.admin.infrastructure.exception.AdminException;
import com.beaker.mintcraft.admin.param.AdminLoginParam;
import com.beaker.mintcraft.admin.valobj.AdminLoginVO;
import com.beaker.mintcraft.api.user.constant.UserRole;
import com.beaker.mintcraft.api.user.request.UserPageQueryRequest;
import com.beaker.mintcraft.api.user.request.UserQueryRequest;
import com.beaker.mintcraft.api.user.response.UserOperatorResponse;
import com.beaker.mintcraft.api.user.response.UserQueryResponse;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import com.beaker.mintcraft.api.user.service.UserManageFacadeService;
import com.beaker.mintcraft.base.response.PageResponse;
import com.beaker.mintcraft.web.util.MultiResultConvertor;
import com.beaker.mintcraft.web.vo.MultiResult;
import com.beaker.mintcraft.web.vo.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import static com.beaker.mintcraft.admin.infrastructure.exception.AdminErrorCode.ADMIN_USER_NOT_EXIST;

/**
 * @Author beaker
 * @Date 2026/6/1 21:06
 * @Description 用户 admin controller
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
public class UserAdminController {

    @DubboReference
    private UserFacadeService userFacadeService;

    @DubboReference
    private UserManageFacadeService userManageFacadeService;

    /**
     * 默认登录超时时间：7天
     */
    private static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;

    @GetMapping("/getUserInfo")
    public Result<UserInfo> getUserInfo() {
        String userId = (String) StpUtil.getLoginId();
        UserQueryRequest request = new UserQueryRequest(Long.valueOf(userId));

        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(request);
        UserInfo userInfo = userQueryResponse.getData();

        if (userInfo == null) {
            throw new AdminException(ADMIN_USER_NOT_EXIST);
        }
        return Result.success(userInfo);
    }

    @GetMapping("/userList")
    public MultiResult<UserInfo> userList(@NotBlank String state, String keyWord, int pageSize, int currentPage) {
        UserPageQueryRequest userPageQueryRequest = new UserPageQueryRequest();
        userPageQueryRequest.setState(state);
        userPageQueryRequest.setKeyWord(keyWord);
        userPageQueryRequest.setCurrentPage(currentPage);
        userPageQueryRequest.setPageSize(pageSize);

        PageResponse<UserInfo> response = userFacadeService.pageQuery(userPageQueryRequest);
        return MultiResultConvertor.convert(response);
    }

    @PostMapping("/registerAdmin")
    public Result<Boolean> registerAdmin(@Valid String phone) {
        //不直接提供管理员注册功能，通过数据订正进行管理员账号初始化
        return null;
    }


    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Valid @RequestBody AdminLoginParam loginParam) {
        // 查询用户信息
        UserQueryRequest userQueryRequest = new UserQueryRequest(loginParam.getTelephone(), loginParam.getPassword());
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
        UserInfo userInfo = userQueryResponse.getData();

        // 用户不存在或不是管理员用户就不能登录
        if (userInfo == null || !userInfo.getUserRole().equals(UserRole.ADMIN)) {
            return Result.error(ADMIN_USER_NOT_EXIST.getCode(), ADMIN_USER_NOT_EXIST.getCode());
        } else {
            // 登录
            StpUtil.login(userInfo.getUserId(), new SaLoginModel()
                    .setIsLastingCookie(loginParam.getRememberMe()).setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
            StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);

            AdminLoginVO loginVO = new AdminLoginVO(userInfo);
            return Result.success(loginVO);
        }
    }

    @PostMapping("/logout")
    public Result<Boolean> logout() {
        StpUtil.logout();

        return Result.success(true);
    }

    @PostMapping("/freeze")
    public Result<UserOperatorResponse> freeze(@Valid Long userId) {
        String adminUserId = (String) StpUtil.getLoginId();

        // 查询用户信息
        UserQueryRequest adminQueryRequest = new UserQueryRequest(Long.valueOf(adminUserId));
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(adminQueryRequest);
        UserInfo userInfo = userQueryResponse.getData();

        // 用户不存在或不是管理员
        if (userInfo == null || !userInfo.getUserRole().equals(UserRole.ADMIN)) {
            return Result.error(ADMIN_USER_NOT_EXIST.getCode(), ADMIN_USER_NOT_EXIST.getMessage());
        }

        // 冻结用户
        UserOperatorResponse response = userManageFacadeService.freeze(userId);

        // 重新查出用户信息, 更新用户 session, 确保权限实时更新
        refreshUserInSession(userId);

        return Result.success(response);
    }

    @PostMapping("/unfreeze")
    private Result<UserOperatorResponse> unfreeze(@Valid Long userId) {
        String adminUserId = (String) StpUtil.getLoginId();

        // 查询用户信息
        UserQueryRequest adminQueryRequest = new UserQueryRequest(Long.valueOf(adminUserId));
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(adminQueryRequest);
        UserInfo userInfo = userQueryResponse.getData();

        // 用户不存在或不是管理员
        if (userInfo == null || !userInfo.getUserRole().equals(UserRole.ADMIN)) {
            return Result.error(ADMIN_USER_NOT_EXIST.getCode(), ADMIN_USER_NOT_EXIST.getMessage());
        }

        // 解冻用户
        UserOperatorResponse response = userManageFacadeService.unfreeze(userId);

        // 重新查出用户信息, 更新用户 session, 确保权限实时更新
        refreshUserInSession(userId);

        return Result.success(response);
    }

    private void refreshUserInSession(Long userId) {
        UserQueryRequest userQueryRequest = new UserQueryRequest(userId);
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);

        StpUtil.getSessionByLoginId(userId).set(userId.toString(), userQueryResponse.getData());
    }
}
