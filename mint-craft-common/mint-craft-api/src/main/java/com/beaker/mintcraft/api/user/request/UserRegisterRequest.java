package com.beaker.mintcraft.api.user.request;

import com.beaker.mintcraft.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/5/2 14:05
 * @Description 用户注册请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest extends BaseRequest {

    private String telephone;

    private String inviteCode;

    private String password;
}
