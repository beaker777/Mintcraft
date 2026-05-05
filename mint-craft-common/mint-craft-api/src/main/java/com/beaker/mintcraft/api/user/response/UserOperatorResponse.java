package com.beaker.mintcraft.api.user.response;

import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.base.response.BaseResponse;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/2 14:12
 * @Description 用户操作响应
 */
@Data
public class UserOperatorResponse extends BaseResponse {

    private UserInfo userInfo;
}
