package com.beaker.mintcraft.order.validator.impl;

import com.beaker.mintcraft.api.order.request.OrderCreateRequest;
import com.beaker.mintcraft.api.user.constant.UserRole;
import com.beaker.mintcraft.api.user.constant.UserState;
import com.beaker.mintcraft.api.user.request.UserQueryRequest;
import com.beaker.mintcraft.api.user.response.UserQueryResponse;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.api.user.service.UserFacadeService;
import com.beaker.mintcraft.order.exception.OrderException;

import static com.beaker.mintcraft.api.order.exception.OrderErrorCode.*;

/**
 * @Author beaker
 * @Date 2026/5/14 16:07
 * @Description 用户校验
 */
public class UserValidator extends BaseOrderCreateValidator {

    private UserFacadeService userFacadeService;

    public UserValidator(UserFacadeService userFacadeService) {
        this.userFacadeService = userFacadeService;
    }

    public UserValidator() {
    }

    @Override
    protected void doValidate(OrderCreateRequest request) throws OrderException {
        String buyerId = request.getBuyerId();
        UserQueryRequest userQueryRequest = new UserQueryRequest(Long.valueOf(buyerId));
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);

        if (userQueryResponse.getSuccess() && userQueryResponse.getData() != null) {
            UserInfo userInfo = userQueryResponse.getData();
            // 校验买家角色
            if (userInfo.getUserRole() != null && !userInfo.getUserRole().equals(UserRole.CUSTOMER)) {
                throw new OrderException(BUYER_IS_PLATFORM_USER);
            }
            // 校验买家状态
            if (userInfo.getState() != null && !userInfo.getState().equals(UserState.ACTIVE.name())) {
                throw new OrderException(BUYER_STATUS_ABNORMAL);
            }
            // 校验买家实名认证状态
            if (userInfo.getCertification() != null && !userInfo.getCertification()) {
                throw  new OrderException(BUYER_NOT_AUTH);
            }
        }
    }
}
