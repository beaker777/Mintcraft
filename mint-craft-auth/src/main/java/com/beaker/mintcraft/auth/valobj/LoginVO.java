package com.beaker.mintcraft.auth.valobj;

import cn.dev33.satoken.stp.StpUtil;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/4/28 19:57
 * @Description 登录 VO
 */
@Data
public class LoginVO {

    private static final long serialVersionUID = 1L;

    /**
     * 用户标识，如用户ID
     */
    private String userId;
    /**
     * 访问令牌
     */
    private String token;

    /**
     * 令牌过期时间
     */
    private Long tokenExpiration;


    public LoginVO(UserInfo userInfo) {
        this.userId = userInfo.getUserId().toString();
        this.token = StpUtil.getTokenValue();
        this.tokenExpiration = StpUtil.getTokenSessionTimeout();
    }
}
