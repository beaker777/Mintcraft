package com.beaker.mintcraft.api.user.request;

import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/9 16:07
 * @Description 用户认证请求
 */
@Data
public class UserAuthRequest {

    private Long userId;

    private String realName;

    private String idCard;
}
