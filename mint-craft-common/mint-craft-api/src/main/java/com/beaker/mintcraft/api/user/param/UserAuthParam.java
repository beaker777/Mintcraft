package com.beaker.mintcraft.api.user.param;

import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/9 16:03
 * @Description 用户实名认证参数
 */
@Data
public class UserAuthParam {

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;
}
