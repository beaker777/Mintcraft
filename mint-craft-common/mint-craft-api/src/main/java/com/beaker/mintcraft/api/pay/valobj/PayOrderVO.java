package com.beaker.mintcraft.api.pay.valobj;

import com.beaker.mintcraft.api.pay.constant.PayOrderState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author beaker
 * @Date 2026/5/22 21:12
 * @Description 支付单 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String payOrderId;

    private String payUrl;

    private PayOrderState orderState;
}
