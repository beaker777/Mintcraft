package com.beaker.mintcraft.admin.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/6/1 22:05
 * @Description 注册参数
 */
@Data
public class AdminRegisterParam {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String telephone;

    /**
     * 验证码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
