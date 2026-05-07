package com.beaker.mintcraft.api.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/5/7 17:09
 * @Description 用户修改请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModifyRequest {

    @NotNull(message = "userId不能为空")
    private Long userId;

    private String nickName;

    private String password;

    private String profilePhotoUrl;

    private String telephone;
}
