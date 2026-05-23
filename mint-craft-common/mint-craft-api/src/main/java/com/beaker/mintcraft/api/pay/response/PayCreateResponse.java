package com.beaker.mintcraft.api.pay.response;

import com.beaker.mintcraft.base.response.BaseResponse;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/23 15:38
 * @Description 支付单创建响应
 */
@Data
public class PayCreateResponse extends BaseResponse {

    private String payOrderId;

    private String payUrl;
}
