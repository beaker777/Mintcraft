package com.beaker.mintcraft.api.pay.service;

import com.beaker.mintcraft.api.pay.request.PayCreateRequest;
import com.beaker.mintcraft.api.pay.response.PayCreateResponse;

/**
 * @Author beaker
 * @Date 2026/5/23 15:43
 * @Description 支付模块 facade 层接口
 */
public interface PayFacadeService {


    /**
     * 生成支付链接
     *
     * @param payCreateRequest
     * @return
     */
    public PayCreateResponse generatePayUrl(PayCreateRequest payCreateRequest);

}
