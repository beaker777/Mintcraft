package com.beaker.mintcraft.api.user.request;

import com.beaker.mintcraft.api.user.request.condition.UserQueryCondition;
import com.beaker.mintcraft.api.user.request.condition.impl.UserIdQueryCondition;
import com.beaker.mintcraft.api.user.request.condition.impl.UserPhoneAndPasswordQueryCondition;
import com.beaker.mintcraft.api.user.request.condition.impl.UserPhoneQueryCondition;
import com.beaker.mintcraft.base.request.BaseRequest;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/4/28 20:19
 * @Description 用户查询请求
 */
@Data
public class UserQueryRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private UserQueryCondition userQueryCondition;

    /**
     * Dubbo 序列化反序列化使用的无参构造器。
     */
    public UserQueryRequest() {
    }

    /**
     * 构造按用户 ID 查询的请求。
     *
     * @param userId 用户 ID
     */
    public UserQueryRequest(Long userId) {
        UserIdQueryCondition userIdQueryCondition = new UserIdQueryCondition();
        userIdQueryCondition.setUserId(userId);
        this.userQueryCondition = userIdQueryCondition;
    }

    /**
     * 构造按手机号查询的请求。
     *
     * @param telephone 手机号
     */
    public UserQueryRequest(String telephone) {
        UserPhoneQueryCondition userPhoneQueryCondition = new UserPhoneQueryCondition();
        userPhoneQueryCondition.setTelephone(telephone);
        this.userQueryCondition = userPhoneQueryCondition;
    }

    /**
     * 构造按手机号和密码查询的请求。
     *
     * @param telephone 手机号
     * @param password 密码
     */
    public UserQueryRequest(String telephone, String password) {
        UserPhoneAndPasswordQueryCondition userPhoneAndPasswordQueryCondition = new UserPhoneAndPasswordQueryCondition();
        userPhoneAndPasswordQueryCondition.setTelephone(telephone);
        userPhoneAndPasswordQueryCondition.setPassword(password);
        this.userQueryCondition = userPhoneAndPasswordQueryCondition;
    }

}
