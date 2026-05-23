package com.beaker.mintcraft.pay.infrastructure.channel.response;

import com.beaker.mintcraft.base.response.BaseResponse;
import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/23 16:21
 * @Description 支付渠道响应
 */
@Data
public class PayChannelResponse extends BaseResponse {

    protected String payUrl;
}
