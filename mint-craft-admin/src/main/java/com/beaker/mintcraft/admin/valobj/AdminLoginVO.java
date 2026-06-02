package com.beaker.mintcraft.admin.valobj;

import cn.dev33.satoken.stp.StpUtil;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/6/1 22:08
 * @Description 登录 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginVO {

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


    public AdminLoginVO(UserInfo userInfo) {
        this.userId = userInfo.getUserId().toString();

        this.token = StpUtil.getTokenValue();
        this.tokenExpiration = StpUtil.getTokenSessionTimeout();
    }
}
