package com.beaker.mintcraft.user.facade;

import com.beaker.mintcraft.api.user.request.UserQueryRequest;
import com.beaker.mintcraft.api.user.request.UserRegisterRequest;
import com.beaker.mintcraft.api.user.request.condition.impl.UserIdQueryCondition;
import com.beaker.mintcraft.api.user.request.condition.impl.UserPhoneAndPasswordQueryCondition;
import com.beaker.mintcraft.api.user.request.condition.impl.UserPhoneQueryCondition;
import com.beaker.mintcraft.api.user.response.UserOperatorResponse;
import com.beaker.mintcraft.api.user.response.UserQueryResponse;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import com.beaker.mintcraft.rpc.facade.Facade;
import com.beaker.mintcraft.user.domain.entity.User;
import com.beaker.mintcraft.user.domain.entity.convertor.UserConvertor;
import com.beaker.mintcraft.user.domain.service.UserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author beaker
 * @Date 2026/4/29 14:17
 * @Description 用户模块 service 层实现类
 */
@DubboService
public class UserFacadeServiceImpl implements UserFacadeService {

    @Resource
    private UserService userService;

    @Facade
    @Override
    public UserOperatorResponse register(UserRegisterRequest userRegisterRequest) {
        return userService.register(userRegisterRequest.getTelephone(), userRegisterRequest.getInviteCode());
    }

    @Facade
    @Override
    public UserQueryResponse<UserInfo> query(UserQueryRequest userQueryRequest) {
        // 根据不同的查询条件进行查询
        User user = switch (userQueryRequest.getUserQueryCondition()) {
            case UserIdQueryCondition userIdQueryCondition:
                yield userService.findById(userIdQueryCondition.getUserId());
            case UserPhoneQueryCondition userPhoneQueryCondition:
                yield userService.findByTelephone(userPhoneQueryCondition.getTelephone());
            case UserPhoneAndPasswordQueryCondition userPhoneAndPasswordQueryCondition:
                yield userService.findByTelephoneAndPassword(userPhoneAndPasswordQueryCondition.getTelephone(), userPhoneAndPasswordQueryCondition.getPassword());
            default:
                throw new UnsupportedOperationException(userQueryRequest.getUserQueryCondition() + " is not supported");
        };

        UserQueryResponse<UserInfo> response = new UserQueryResponse<>();
        UserInfo userInfo = UserConvertor.INSTANCE.mapToVO(user);
        response.setSuccess(true);
        response.setData(userInfo);

        return response;
    }
}
