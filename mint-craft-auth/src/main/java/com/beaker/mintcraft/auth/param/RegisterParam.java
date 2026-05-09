package com.beaker.mintcraft.auth.param;

import com.beaker.mintcraft.base.validator.IsMobile;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/4/28 20:09
 * @Description 注册信息
 */
@Data
public class RegisterParam {

    /**
     * 手机号
     */
    @IsMobile
    private String telephone;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String captcha;

    /**
     * 邀请码
     */
    private String inviteCode;
}
