package com.beaker.mintcraft.api.user.request;

import com.beaker.mintcraft.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beaker
 * @Date 2026/5/24 18:37
 * @Description 用户激活请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActiveRequest extends BaseRequest {

    private Long userId;

    private String blockChainPlatform;

    private String blockChainUrl;
}
