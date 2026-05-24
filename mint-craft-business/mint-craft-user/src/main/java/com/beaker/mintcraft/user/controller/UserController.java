package com.beaker.mintcraft.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.beaker.mintcraft.api.chain.request.ChainProcessRequest;
import com.beaker.mintcraft.api.chain.response.ChainProcessResponse;
import com.beaker.mintcraft.api.chain.response.data.ChainCreateData;
import com.beaker.mintcraft.api.chain.service.ChainFacadeService;
import com.beaker.mintcraft.api.user.param.UserAuthParam;
import com.beaker.mintcraft.api.user.param.UserModifyParam;
import com.beaker.mintcraft.api.user.request.UserActiveRequest;
import com.beaker.mintcraft.api.user.request.UserAuthRequest;
import com.beaker.mintcraft.api.user.request.UserModifyRequest;
import com.beaker.mintcraft.api.user.response.UserOperatorResponse;
import com.beaker.mintcraft.api.user.response.data.BasicUserInfo;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.file.FileService;
import com.beaker.mintcraft.user.domain.entity.User;
import com.beaker.mintcraft.user.domain.entity.convertor.UserConvertor;
import com.beaker.mintcraft.user.domain.service.UserService;
import com.beaker.mintcraft.user.infrastructure.exception.UserException;
import com.beaker.mintcraft.web.vo.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static com.beaker.mintcraft.api.common.constant.CommonConstant.APP_NAME_UPPER;
import static com.beaker.mintcraft.api.common.constant.CommonConstant.SEPARATOR;
import static com.beaker.mintcraft.user.infrastructure.exception.UserErrorCode.*;

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

    @Autowired
    private FileService fileService;

    @DubboReference
    private ChainFacadeService chainFacadeService;

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

    @PostMapping("/modifyProfilePhoto")
    public Result<String> modifyProfilePhoto(@RequestParam("file_data") MultipartFile file, InputStream inputStream) throws Exception {
        String userId = (String) StpUtil.getLoginId();
        String prefix = "https://mintcraft.oss-cn-beijing.aliyuncs.com/";

        if (file == null) {
            throw new UserException(USER_UPLOAD_PICTURE_FAIL);
        }

        // 上传文件到阿里云 OSS
        String filename = file.getOriginalFilename();
        InputStream fileStream = file.getInputStream();
        String path = "profile" + userId + "/" + filename;
        boolean res = fileService.upload(path, fileStream);

        if (!res) {
            throw new UserException(USER_UPLOAD_PICTURE_FAIL);
        }

        // 修改信息
        UserModifyRequest userModifyRequest = new UserModifyRequest();
        userModifyRequest.setUserId(Long.valueOf(userId));
        userModifyRequest.setProfilePhotoUrl(prefix + path);

        Boolean modifyResult = userService.modify(userModifyRequest).getSuccess();
        if (!modifyResult) {
            throw new UserException(USER_UPLOAD_PICTURE_FAIL);
        }
        return Result.success(prefix + path);
    }

    @PostMapping("/auth")
    public Result<Boolean> auth(@Valid @RequestBody UserAuthParam userAuthParam) {
        // 获取 userId
        String userId = (String) StpUtil.getLoginId();

        // 实名认证
        UserAuthRequest userAuthRequest = new UserAuthRequest();
        userAuthRequest.setUserId(Long.valueOf(userId));
        userAuthRequest.setRealName(userAuthParam.getRealName());
        userAuthRequest.setIdCard(userAuthParam.getIdCard());
        UserOperatorResponse authResult = userService.auth(userAuthRequest);

        // 实名认证成功, 进行上链
        if (authResult.getSuccess()) {
            ChainProcessRequest chainCreateRequest = new ChainProcessRequest();

            chainCreateRequest.setUserId(userId);
            String identifier = APP_NAME_UPPER + SEPARATOR + authResult.getUserInfo().getUserRole() + SEPARATOR + authResult.getUserInfo().getUserId();
            chainCreateRequest.setIdentifier(identifier);

            ChainProcessResponse<ChainCreateData> chainProcessResponse = chainFacadeService.createAddr(chainCreateRequest);
            if (chainProcessResponse.getSuccess()) {
                // 第三方注册上链账户成功
                ChainCreateData chainCreateData = chainProcessResponse.getData();

                // 激活账户
                UserActiveRequest userActiveRequest = new UserActiveRequest();
                userActiveRequest.setUserId(Long.valueOf(userId));
                userActiveRequest.setBlockChainUrl(chainCreateData.getAccount());
                userActiveRequest.setBlockChainPlatform(chainCreateData.getPlatform());

                UserOperatorResponse activeResponse = userService.active(userActiveRequest);
                if (activeResponse.getSuccess()) {
                    // 激活成功, 刷新 session 中的用户信息
                    refreshUserInSession(userId);
                    return Result.success(true);
                }

                // 激活账户失败
                return Result.error(activeResponse.getResponseCode(), activeResponse.getResponseMessage());
            } else {
                // 创建链账户失败
                throw new UserException(USER_CREATE_CHAIN_FAIL);
            }
        }

        return Result.error(authResult.getResponseCode(), authResult.getResponseMessage());
    }

    private void refreshUserInSession(String userId) {
        User user = userService.getById(userId);
        UserInfo userInfo = UserConvertor.INSTANCE.mapToVO(user);

        StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
    }
}
