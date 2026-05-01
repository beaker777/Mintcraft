package com.beaker.mintcraft.auth.param;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author beaker
 * @Date 2026/4/28 20:09
 * @Description 登录参数
 */
@Getter
@Setter
public class LoginParam extends RegisterParam {

    /**
     * 记住我
     */
    private Boolean rememberMe;
}
