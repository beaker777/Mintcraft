package com.beaker.mintcraft.pay.infrastructure.channel.request;

import com.beaker.mintcraft.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author beaker
 * @Date 2026/5/23 16:22
 * @Description 支付渠道请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayChannelRequest extends BaseRequest {

    /**
     * 支付单号
     */
    private String orderId;

    /**
     * 金额
     * 单位：分
     */
    private Long amount;

    /**
     * 订单描述
     */
    private String description;

    /**
     * 附加信息
     */
    private String attach;

    /**
     * 超时时间
     */
    private Date expireTime;
}
