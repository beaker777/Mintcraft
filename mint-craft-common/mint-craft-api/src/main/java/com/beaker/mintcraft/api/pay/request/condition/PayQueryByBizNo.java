package com.beaker.mintcraft.api.pay.request.condition;

import lombok.Data;

/**
 * @Author beaker
 * @Date 2026/5/31 18:42
 * @Description 根据 BizNo 查询
 */
@Data
public class PayQueryByBizNo implements PayQueryCondition {

    private String bizNo;

    private String bizType;
}
