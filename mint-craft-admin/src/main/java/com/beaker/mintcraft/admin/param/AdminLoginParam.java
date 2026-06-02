package com.beaker.mintcraft.admin.param;

import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/6/1 22:06
 * @Description 登录参数
 */
@Data
public class AdminLoginParam extends AdminRegisterParam {

    /**
     * 记住我
     */
    private Boolean rememberMe;
}
